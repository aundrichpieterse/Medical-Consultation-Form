package com.aundrich.bookingplatform.servlet;

import com.aundrich.bookingplatform.DatabaseConnection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.http.HttpSession;

@WebServlet("/ConsultationServlet")
public class ConsultationServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter(); Connection conn = DatabaseConnection.getConnection()) {
            String idNumber = request.getParameter("idNumber");
            String name = request.getParameter("name");
            String surname = request.getParameter("surname");
            String middlename = request.getParameter("middlename");
            String birthdate = request.getParameter("birthdate");
            String cellNumber = request.getParameter("cellNumber");
            String consultationDate = request.getParameter("consultationDate");
            String consultationSlot = request.getParameter("consultationSlot");
            String consultationReason = request.getParameter("consultationReason");

            if (conn == null) {
                out.println("<h2>Connection failed!</h2>");
                return;
            }

            if (checkPatientExists(conn, idNumber, out)) {
                int patientId = getPatientId(conn, idNumber);
                checkExistingBooking(out, conn, patientId, consultationDate, consultationSlot, consultationReason);
            } else {
                insertNewPatient(out, conn, idNumber, name, surname, middlename, birthdate, cellNumber, consultationDate, consultationSlot, consultationReason);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("<h2>Booking failed!</h2>");
            response.getWriter().println("<p>There was an error: " + e.getMessage() + "</p>");
        }
    }

    private boolean checkPatientExists(Connection conn, String idNumber, PrintWriter out) throws Exception {
        String checkPatientSQL = "SELECT id FROM patient_list WHERE id_number = ?";
        try (PreparedStatement checkPatientStmt = conn.prepareStatement(checkPatientSQL)) {
            checkPatientStmt.setString(1, idNumber);
            ResultSet rs = checkPatientStmt.executeQuery();
            return rs.next();
        }
    }

    private int getPatientId(Connection conn, String idNumber) throws Exception {
        String getIdSQL = "SELECT id FROM patient_list WHERE id_number = ?";
        try (PreparedStatement getIdStmt = conn.prepareStatement(getIdSQL)) {
            getIdStmt.setString(1, idNumber);
            ResultSet rs = getIdStmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
            throw new Exception("Patient ID not found");
        }
    }

    private void checkExistingBooking(PrintWriter out, Connection conn, int patientId, String consultationDate, String consultationSlot, String consultationReason) throws Exception {
        String checkBookingSQL = "SELECT COUNT(*) FROM consultation_table WHERE patient_id = ? AND cdate = ? AND cperiod = ?";
        try (PreparedStatement checkBookingStmt = conn.prepareStatement(checkBookingSQL)) {
            checkBookingStmt.setInt(1, patientId);
            checkBookingStmt.setString(2, consultationDate);
            checkBookingStmt.setString(3, consultationSlot);
            ResultSet bookingRs = checkBookingStmt.executeQuery();

            if (bookingRs.next() && bookingRs.getInt(1) > 0) {
                out.println("<h2>Booking failed!</h2>");
                out.println("<p>You already have a booking for this date and time slot.</p>");
            } else {
                // Insert new consultation
                insertConsultation(out, conn, patientId, consultationDate, consultationSlot, consultationReason);
            }
        }
    }

    private void insertConsultation(PrintWriter out, Connection conn, int patientId, String consultationDate, String consultationSlot, String consultationReason) throws Exception {
        String insertConsultationSQL = "INSERT INTO consultation_table (patient_id, cdate, cperiod, cReason) VALUES (?, ?, ?, ?)";
        try (PreparedStatement insertConsultationStmt = conn.prepareStatement(insertConsultationSQL)) {
            insertConsultationStmt.setInt(1, patientId);
            insertConsultationStmt.setString(2, consultationDate);
            insertConsultationStmt.setString(3, consultationSlot);
            insertConsultationStmt.setString(4, consultationReason);
            int rowsInserted = insertConsultationStmt.executeUpdate();

            if (rowsInserted > 0) {
                out.println("<h2>Booking successful!</h2>");
                out.println("<p>Your appointment has been booked.</p>");
                out.println("<p><a href='ViewBookingsServlet'>View Your Bookings</a></p>");
            } else {
                out.println("<h2>Booking failed!</h2>");
                out.println("<p>There was an error in booking your appointment.</p>");
            }
        }
    }

    private void insertNewPatient(PrintWriter out, Connection conn, String idNumber, String name, String surname, String middlename, String birthdate, String cellNumber, String consultationDate, String consultationSlot, String consultationReason) throws Exception {
        String insertPatientSQL = "INSERT INTO patient_list (id_number, name, surname, middlename, birthdate, tell) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement insertPatientStmt = conn.prepareStatement(insertPatientSQL, PreparedStatement.RETURN_GENERATED_KEYS)) {
            insertPatientStmt.setString(1, idNumber);
            insertPatientStmt.setString(2, name);
            insertPatientStmt.setString(3, surname);
            insertPatientStmt.setString(4, middlename);

            java.sql.Date sqlBirthdate = java.sql.Date.valueOf(birthdate);
            insertPatientStmt.setDate(5, sqlBirthdate);

            insertPatientStmt.setString(6, cellNumber);

            int rowsInserted = insertPatientStmt.executeUpdate();

            if (rowsInserted > 0) {
                try (ResultSet generatedKeys = insertPatientStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int patientId = generatedKeys.getInt(1);
                        insertConsultation(out, conn, patientId, consultationDate, consultationSlot, consultationReason);
                        
                        HttpSession session = request.getSession();
                        session.setAttribute("patientId", patientId);
                    }
                }
            } else {
                out.println("<h2>Booking failed!</h2>");
                out.println("<p>There was an error in adding your information.</p>");
            }
        }
    }
}
