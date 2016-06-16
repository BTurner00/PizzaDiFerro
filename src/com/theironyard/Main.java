package com.theironyard;

import jodd.json.JsonParser;
import jodd.json.JsonSerializer;
import org.h2.tools.Server;
import spark.Spark;

import java.sql.*;
import java.util.ArrayList;

public class Main {

    public static void createTables(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE IF NOT EXISTS messages (id IDENTITY, author VARCHAR, text VARCHAR)");
    }

    public static void insertMessage(Connection conn, Pizza msg) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO messages VALUES (NULL, ?, ?)");
        stmt.setString(1, msg.author);
        stmt.setString(2, msg.text);
        stmt.execute();
    }

    public static ArrayList<Pizza> selectMessage(Connection conn) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM messages");
        ResultSet results = stmt.executeQuery();
        ArrayList<Pizza> msgs = new ArrayList<>();
        while(results.next()) {
            int id = results.getInt("id");
            String author = results.getString("author");
            String text = results.getString("text");
            Pizza msg = new Pizza(id, author, text);
            msgs.add(msg);
        }
        return msgs;
    }

    public static

    public static void main(String[] args) throws SQLException {
        Server.createWebServer().start();
        Connection conn = DriverManager.getConnection("jdbc:h2:./main");
        createTables(conn);

        Spark.externalStaticFileLocation("public");
        Spark.init();

        JsonSerializer serializer = new JsonSerializer();

        Spark.get(
                "/get-messages",
                (request, response) -> {
                    ArrayList<Pizza> msgs = selectMessage(conn);
                    return serializer.serialize(msgs);
                }

        );

        Spark.post(
                "/add-message",
                (request, response) -> {
                    String body = request.body();
                    JsonParser p = new JsonParser();
                    Pizza msg = p.parse(body, Pizza.class);
                    insertMessage(conn, msg);
                    return "";
                }

        );

        Spark.put(
                "/edit-message",
                (request, response) -> {
                    //update message in db
                    return "";
                }

        );

        Spark.delete(
                "/delete-message",
                (request, response) -> {
                    //delete message in db
                    return "";
                }

        );

    }
}
