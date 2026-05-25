package app.controllers;

import app.entities.BillOfMaterial;
import app.persistence.BillOfMaterialMapper;
import app.persistence.CarportRequestMapper;
import app.persistence.OfferMapper;
import app.persistence.UserMapper;
import app.persistence.ZipCodeMapper;
import app.services.Calculator;
import io.javalin.http.Context;

import java.sql.SQLException;

public class UserController {

     public static void createUser(Context ctx) {

        String name = ctx.formParam("name");
        String address = ctx.formParam("address");
        String email = ctx.formParam("email");
        String zipCode = ctx.formParam("zipCode");
        String phone = ctx.formParam("phone");
        String password = ctx.formParam("password");
        // Sikrer, at rollen ikke kan ændres til admin
        String role = "customer";

        ctx.attribute("name", name);
        ctx.attribute("address", address);
        ctx.attribute("email", email);
        ctx.attribute("zipCode", zipCode);
        ctx.attribute("phone", phone);

        try {

            if (address == null || !address.matches(".*[A-Za-zÆØÅæøå].*") || !address.matches(".*\\d.*")) {
                ctx.attribute("errorField", "address");
                ctx.attribute("errorMessage", "Adresse skal indeholde både vejnavn og husnummer.");
                ctx.render("create_user.html");
                return;
            }

            if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$") || UserMapper.emailExists(email)) {
                ctx.attribute("errorField", "email");
                ctx.attribute("errorMessage", "Mailen findes allerede eller er ugyldig.");
                ctx.render("create_user.html");
                return;
            }

            if (zipCode == null || !zipCode.matches("\\d{4}")) {
                ctx.attribute("errorField", "zipCode");
                ctx.attribute("errorMessage", "Postnummer skal bestå af 4 cifre.");
                ctx.render("create_user.html");
                return;
            }

            int zipCodeInt = Integer.parseInt(zipCode);

            if (!ZipCodeMapper.zipCodeExists(zipCodeInt)) {
                ctx.attribute("errorField", "zipCode");
                ctx.attribute("errorMessage", "Postnummeret findes ikke.");
                ctx.render("create_user.html");
                return;
            }

            if (phone == null || !phone.matches("\\d{8}")) {
                ctx.attribute("errorField", "phone");
                ctx.attribute("errorMessage", "Telefonnummer skal bestå af 8 cifre.");
                ctx.render("create_user.html");
                return;
            }

            int userId = UserMapper.createUser(
                    name, password, email, phone, address, zipCodeInt, role
            );

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

            ctx.sessionAttribute("userId", userId);
            ctx.sessionAttribute("email", email);

            ctx.redirect("/user");

        } catch (SQLException e) {
            e.printStackTrace();
            ctx.attribute("error", "Brugeren kunne ikke oprettes.");
            ctx.render("create_user.html");
        }
    }
}