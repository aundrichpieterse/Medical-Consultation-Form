<%-- 
    Document   : viewBookings
    Created on : Oct 3, 2024, 11:38:35 AM
    Author     : Aundrich Pieterse
--%>

<%@ page import="java.sql.*, java.util.*" %>
<!DOCTYPE html>
<html>
<head>
    <title>View Patient Details</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            padding: 20px;
        }

        h2 {
            text-align: center;
            color: #333;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            margin: 20px 0;
        }

        table, th, td {
            border: 1px solid #ddd;
        }

        th, td {
            padding: 10px;
            text-align: left;
        }

        th {
            background-color: #008CBA;
            color: white;
        }

        tr:nth-child(even) {
            background-color: #f2f2f2;
        }

        .back-button {
            display: block;
            width: 150px;
            margin: 20px auto;
            padding: 10px;
            text-align: center;
            background-color: #008CBA;
            color: white;
            text-decoration: none;
            border-radius: 5px;
        }

        .back-button:hover {
            background-color: #005f73;
        }
    </style>
</head>
<body>

    <h2>Patient List</h2>

    <table>
        <tr>
            <th>ID Number</th>
            <th>Name</th>
            <th>Surname</th>
            <th>Middle Name</th>
            <th>Birthdate</th>
            <th>Cell Number</th>
        </tr>
        <%
            Connection conn = null;
            Statement stmt = null;
            ResultSet rs = null;

            try {
                // Connect to the database
                Class.forName("com.mysql.cj.jdbc.Driver");
                conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/medical_consulting_db?useSSL=false", "", "");
                stmt = conn.createStatement();
                
                // Query the patient_list table
                String query = "SELECT id_number, name, surname, middlename, birthdate, tell FROM patient_list";
                rs = stmt.executeQuery(query);

                // Display the patients in the table
                while (rs.next()) {
                    String idNumber = rs.getString("id_number");
                    String name = rs.getString("name");
                    String surname = rs.getString("surname");
                    String middlename = rs.getString("middlename") == null ? "" : rs.getString("middlename");
                    String birthdate = rs.getString("birthdate");
                    String tell = rs.getString("tell");

                    out.println("<tr>");
                    out.println("<td>" + idNumber + "</td>");
                    out.println("<td>" + name + "</td>");
                    out.println("<td>" + surname + "</td>");
                    out.println("<td>" + middlename + "</td>");
                    out.println("<td>" + birthdate + "</td>");
                    out.println("<td>" + tell + "</td>");
                    out.println("</tr>");
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            }
        %>
    </table>

    <a href="index.jsp" class="back-button">Back to Form</a>

</body>
</html>
