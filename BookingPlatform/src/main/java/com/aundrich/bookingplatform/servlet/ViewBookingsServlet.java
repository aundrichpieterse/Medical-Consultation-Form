package com.aundrich.bookingplatform.servlet;

import com.aundrich.bookingplatform.Booking;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.RequestDispatcher;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

@WebServlet("/ViewBookingsServlet")
public class ViewBookingsServlet extends HttpServlet {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/medical_consulting_db";
    private static final String DB_USER = "";
    private static final String DB_PASSWORD = "";

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        ArrayList<Booking> bookings = new ArrayList<>();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement statement = connection.prepareStatement(
                     "SELECT p.name, p.surname, c.cdate, s.periods, c.cReason " +
                     "FROM consultation_table c " +
                     "JOIN patient_list p ON c.patient_id = p.id " +
                     "JOIN consultation_schedule s ON c.cperiod = s.id " +
                     "ORDER BY c.cdate ASC, c.cperiod ASC")) {
                
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    Booking booking = new Booking();
                    booking.setName(resultSet.getString("name"));
                    booking.setSurname(resultSet.getString("surname"));
                    booking.setConsultationDate(resultSet.getDate("cdate"));
                    booking.setConsultationPeriod(resultSet.getString("periods"));
                    booking.setConsultationReason(resultSet.getString("cReason"));
                    bookings.add(booking);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Unable to retrieve bookings. Please try again later.");
        }

        request.setAttribute("bookings", bookings);
        RequestDispatcher dispatcher = request.getRequestDispatcher("viewBookings.jsp");
        dispatcher.forward(request, response);
    }
}
