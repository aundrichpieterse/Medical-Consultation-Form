<%-- 
    Document   : index
    Created on : Oct 3, 2024, 8:16:58 AM
    Author     : Aundrich Pieterse
--%>

<%@ page import="java.sql.*, java.util.*" %>
<!DOCTYPE html>
<html>
<head>
    <title>Medical Consultation Form</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            margin: 0;
            padding: 20px;
        }

        h2 {
            text-align: center;
            color: #333;
        }

        form {
            background-color: #fff;
            border-radius: 10px;
            box-shadow: 0px 0px 10px rgba(0, 0, 0, 0.1);
            max-width: 600px;
            margin: 0 auto;
            padding: 20px;
        }

        label {
            display: block;
            font-weight: bold;
            margin-bottom: 5px;
            color: #333;
        }

        input[type="text"], input[type="date"], select, textarea {
            width: 100%;
            padding: 10px;
            margin-bottom: 20px;
            border: 1px solid #ccc;
            border-radius: 5px;
            box-sizing: border-box;
        }

        input[type="submit"] {
            background-color: #008CBA;
            color: white;
            margin-left: 200px;
            padding: 12px 20px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            width: 35%;
            font-size: 16px;
        }

        input[type="submit"]:hover {
            background-color: #005f73;
        }

        .button-container {
            text-align: center;
            margin-bottom: 20px;
        }

        .button-container a {
            background-color: #008CBA;
            color: white;
            padding: 10px 20px;
            text-decoration: none;
            margin: 10px;
            border-radius: 5px;
            display: inline-block;
        }

        .button-container a:hover {
            background-color: #005f73;
        }
    </style>
</head>
<body>

    <h2>Schedule a Consultation</h2>

    <div class="button-container">
        <a href="viewBookings.jsp">View Bookings</a>
    </div>

    <form action="SubmitServlet" method="post">
        <!-- Patient Details -->
        <label for="idNumber">ID Number:</label>
        <input type="text" id="idNumber" name="idNumber" maxlength="13" required><br>
        
        <label for="name">Name:</label>
        <input type="text" id="name" name="name" required><br>
        
        <label for="surname">Surname:</label>
        <input type="text" id="surname" name="surname" required><br>
        
        <label for="middlename">Middle Name (optional):</label>
        <input type="text" id="middlename" name="middlename"><br>
        
        <label for="birthdate">Birthdate:</label>
        <input type="date" id="birthdate" name="birthdate" required><br>
        
        <label for="tell">Cell Number:</label>
        <input type="text" id="tell" name="tell" maxlength="10" required><br>
        
        <!-- Consultation Details -->
        <label for="consultationDate">Consultation Date:</label>
        <input type="date" id="consultationDate" name="consultationDate" required><br>

        <label for="consultationSlot">Consultation Slot:</label>
        <select id="consultationSlot" name="consultationSlot" required>
            <option value="">Select a slot</option>
            <%
                Connection conn = null;
                Statement stmt = null;
                ResultSet rs = null;

                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/medical_consulting_db?useSSL=false", "", "");
                    stmt = conn.createStatement();
                    
                    String query = "SELECT id, periods FROM consultation_schedule";
                    rs = stmt.executeQuery(query);
                    
                    while (rs.next()) {
                        int slotId = rs.getInt("id");
                        String period = rs.getString("periods");
                        out.println("<option value=\"" + slotId + "\">" + period + "</option>");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (rs != null) rs.close();
                    if (stmt != null) stmt.close();
                    if (conn != null) conn.close();
                }
            %>
        </select><br>
        
        <label for="reason">Reason for Consultation:</label><br>
        <textarea id="reason" name="reason" rows="4" cols="50" required></textarea><br>
        
        <input type="submit" value="Book Consultation">
    </form>
        <% String successMessage = (String) request.getAttribute("successMessage"); %>
<% String errorMessage = (String) request.getAttribute("errorMessage"); %>

<% if (successMessage != null) { %>
    <p style="color: green; text-align: center;"><%= successMessage %></p>
<% } else if (errorMessage != null) { %>
    <p style="color: red; text-align: center;"><%= errorMessage %></p>
<% } %>

</body>
</html>
