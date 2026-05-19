package app;

import app.config.ThymeleafConfig;
import app.controllers.UserController;
import app.entities.BOMLine;
import app.entities.BillOfMaterial;
import app.entities.Offer;
import app.entities.Order;
import app.persistence.BillOfMaterialMapper;
import app.persistence.CarportRequestMapper;
import app.persistence.OfferMapper;
import app.persistence.UserMapper;
import app.persistence.OrderMapper;
import app.services.Calculator;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;

import java.sql.SQLException;
import java.util.List;


public class Main {



    public static void main(String[] args) {

        Javalin app = Javalin.create(config -> {
            config.fileRenderer(new JavalinThymeleaf(ThymeleafConfig.templateEngine()));
            config.staticFiles.add("/public");
        }).start(7070);

        app.get("/", ctx -> ctx.render("index.html"));


        app.post("/offer", ctx -> {
            String length = ctx.formParam("length");
            String width = ctx.formParam("width");

            if (!isOnlyDigits(length) || !isOnlyDigits(width)) {
                ctx.attribute("error", "Længde og bredde må kun indeholde tal.");
                ctx.render("index.html");
                return;
            }

            // >>> DEFINES AND ENFORCES MIN AND MAX VALUES <<<

            int lengthInt = Integer.parseInt(length);
            int widthInt = Integer.parseInt(width);

            if (lengthInt < 240 || widthInt < 240 || lengthInt > 720 || widthInt > 600) {
                ctx.attribute("error", "De angivne mål er uden for standardrammen.");
                ctx.render("index.html");
                return;
            }

            // >>> CALCULATES TOTAL PRICE <<<
            Calculator calculator = new Calculator(lengthInt, widthInt, null);
            BillOfMaterial billOfMaterial = calculator.calcCarport();

            int price = (int) Math.round(billOfMaterial.getTotalPrice());

            ctx.sessionAttribute("length", length);
            ctx.sessionAttribute("width", width);
            ctx.sessionAttribute("price", price);

            ctx.attribute("length", length);
            ctx.attribute("width", width);
            ctx.attribute("price", price);

            ctx.render("offer.html");
        });

        app.get("/create-user", ctx -> ctx.render("create_user.html"));

        app.post("/create-user", ctx -> UserController.createUser(ctx));

        app.get("/login", ctx -> ctx.render("login.html"));

        app.post("/login", ctx -> {
            String email = ctx.formParam("email");
            String password = ctx.formParam("password");

            try {
                Integer userId = UserMapper.login(email, password);

                if (userId != null) {
                    ctx.sessionAttribute("userId", userId);
                    ctx.sessionAttribute("email", email);

                    String length = ctx.sessionAttribute("length");
                    String width = ctx.sessionAttribute("width");
                    Integer price = ctx.sessionAttribute("price");

                    if (length != null && width != null && price != null) {
                        int requestId = CarportRequestMapper.createRequest(userId, length, width);
                        int offerId = OfferMapper.createOffer(requestId, price);

                        Calculator calculator = new Calculator(Integer.parseInt(length), Integer.parseInt(width), null);
                        BillOfMaterial billOfMaterial = calculator.calcCarport();

                        BillOfMaterialMapper.saveBillOfMaterial(offerId, billOfMaterial);

                        ctx.sessionAttribute("length", null);
                        ctx.sessionAttribute("width", null);
                        ctx.sessionAttribute("price", null);
                    }

                    ctx.redirect("/user");
                } else {
                    ctx.attribute("error", "Forkert e-mail eller adgangskode.");
                    ctx.render("login.html");
                }

            } catch (SQLException e) {
                e.printStackTrace();
                ctx.attribute("error", "Der opstod en fejl ved login.");
                ctx.render("login.html");
            }
        });

        app.get("/user", ctx -> {

            String error = ctx.sessionAttribute("error");
            ctx.attribute("error", error);
            ctx.sessionAttribute("error", null);

            Integer userId = ctx.sessionAttribute("userId");
            String email = ctx.sessionAttribute("email");

            if (userId == null) {
                ctx.redirect("/login");
                return;
            }

            try {
                List<Offer> offers = OfferMapper.getOffersByUserId(userId);
                List<Order> orders = OrderMapper.getOrdersByUserId(userId);

                ctx.attribute("email", email);
                ctx.attribute("offers", offers);
                ctx.attribute("orders", orders);

                ctx.render("user.html");

            } catch (SQLException e) {
                e.printStackTrace();
                ctx.attribute("error", "Tilbud og ordrer kunne ikke hentes.");
                ctx.render("user.html");
            }
        });

        app.post("/user/create-offer", ctx -> {

            Integer userId = ctx.sessionAttribute("userId");

            if (userId == null) {
                ctx.redirect("/login");
                return;
            }

            String length = ctx.formParam("length");
            String width = ctx.formParam("width");

            if (!isOnlyDigits(length) || !isOnlyDigits(width)) {
                ctx.sessionAttribute("error", "Længde og bredde må kun indeholde tal.");
                ctx.redirect("/user");
                return;
            }

            int lengthInt = Integer.parseInt(length);
            int widthInt = Integer.parseInt(width);

            if (lengthInt < 240 || widthInt < 240 || lengthInt > 720 || widthInt > 600) {
                ctx.sessionAttribute("error", "De angivne mål er uden for standardrammen.");
                ctx.redirect("/user");
                return;
            }

            try {
                Calculator calculator = new Calculator(lengthInt, widthInt, null);
                BillOfMaterial billOfMaterial = calculator.calcCarport();

                int price = (int) Math.round(billOfMaterial.getTotalPrice());

                int requestId = CarportRequestMapper.createRequest(userId, length, width);

                int offerId = OfferMapper.createOffer(requestId, price);
                BillOfMaterialMapper.saveBillOfMaterial(offerId, billOfMaterial);

                ctx.redirect("/user");

            } catch (SQLException e) {
                e.printStackTrace();
                ctx.sessionAttribute("error", "Tilbuddet kunne ikke oprettes.");
                ctx.redirect("/user");
            }
        });

        app.post("/reject-offer", ctx -> {
            int offerId = Integer.parseInt(ctx.formParam("offerId"));

            try {
                OfferMapper.deleteOfferById(offerId);
                ctx.redirect("/user");

            } catch (SQLException e) {
                e.printStackTrace();
                ctx.sessionAttribute("error", "Tilbuddet kunne ikke afvises.");
                ctx.redirect("/user");
            }
        });

        app.post("/accept-offer", ctx -> {
            Integer userId = ctx.sessionAttribute("userId");

            if (userId == null) {
                ctx.redirect("/login");
                return;
            }

            int offerId = Integer.parseInt(ctx.formParam("offerId"));

            try {
                OrderMapper.createOrder(offerId, userId);
                ctx.redirect("/user");

            } catch (SQLException e) {
                e.printStackTrace();
                ctx.sessionAttribute("error", "Tilbuddet kunne ikke accepteres.");
                ctx.redirect("/user");
            }
        });

        app.post("/order/bom", ctx -> {
            int offerId = Integer.parseInt(ctx.formParam("offerId"));

            try {
                List<BOMLine> bomLines = BillOfMaterialMapper.getBOMLinesByOfferId(offerId);

                ctx.attribute("bomLines", bomLines);
                ctx.render("bom.html");

            } catch (SQLException e) {
                e.printStackTrace();
                ctx.sessionAttribute("error", "Styklisten kunne ikke hentes.");
                ctx.redirect("/user");
            }
        });

    }


    private static boolean isOnlyDigits(String value) {
        return value != null && value.matches("\\d+");
    }

}