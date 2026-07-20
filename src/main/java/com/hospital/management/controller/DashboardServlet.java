package com.hospital.management.controller;
import javax.servlet.*;import javax.servlet.annotation.WebServlet;import javax.servlet.http.*;import java.io.IOException;
@WebServlet("/dashboard") public class DashboardServlet extends HttpServlet {protected void doGet(HttpServletRequest q,HttpServletResponse r)throws ServletException,IOException{q.getRequestDispatcher("/WEB-INF/views/dashboard/dashboard.jsp").forward(q,r);}}
