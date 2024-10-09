package com.aundrich.bookingplatform.servlet;

import java.io.IOException;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/SubmitServlet")
public class SubmitServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        String idNumber = request.getParameter("idNumber");
        String name = request.getParameter("name");
        String surname = request.getParameter("surname");
        String middlename = request.getParameter("middlename");
        String birthdate = request.getParameter("birthdate");
        String tell = request.getParameter("tell");
        String consultationDate = request.getParameter("consultationDate");
        String consultationSlot = request.getParameter("consultationSlot");
        String reason = request.getParameter("reason");

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/medical_consulting_db?useSSL=false", "", "");

            String insertPatientQuery = "INSERT INTO patient_list (name, surname, middlename, birthdate, tell, id_number) VALUES (?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(insertPatientQuery, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, name);
            pstmt.setString(2, surname);
            pstmt.setString(3, middlename);
            pstmt.setDate(4, Date.valueOf(birthdate));
            pstmt.setString(5, tell);
            pstmt.setString(6, idNumber);
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            rs.next();
            int patientId = rs.getInt(1);

            String insertConsultationQuery = "INSERT INTO consultation_table (patient_id, cdate, cperiod, cReason) VALUES (?, ?, ?, ?)";
            pstmt = conn.prepareStatement(insertConsultationQuery);
            pstmt.setInt(1, patientId);
            pstmt.setDate(2, Date.valueOf(consultationDate));
            pstmt.setInt(3, Integer.parseInt(consultationSlot));
            pstmt.setString(4, reason);
            pstmt.executeUpdate();

            request.setAttribute("successMessage", "Consultation scheduled successfully!");
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error scheduling consultation.");
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }

        request.getRequestDispatcher("index.jsp").forward(request, response);
    }
}
