/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sait.cprg352;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author 636334
 */
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException 
    {
        String action = request.getParameter("action");
        
        Cookie[] cookies = request.getCookies();
        
        String cookieName = "userCookie";
        
        HttpSession session = request.getSession();

        if(action!=null)
        {
            for(Cookie cookie: cookies)
            {
                if(cookieName.equals(cookie.getName()))
                {
                    request.setAttribute("user", cookie.getValue());
                }
            }
            getServletContext().getRequestDispatcher("/WEB-INF/login.jsp").forward(request, response);
            session.invalidate();
        }
        else if (session.getAttribute("user")!=null)
        {
            response.sendRedirect("home");
        }
        else if(cookies!=null)
        {
            for(Cookie cookie:cookies)
            {
                if(cookieName.equals(cookie.getName()))
                {
                    request.setAttribute("user", cookie.getValue());
                }
                getServletContext().getRequestDispatcher("/WEB-INF/login.jsp").forward(request, response);
            }
        }
        else
        {
            getServletContext().getRequestDispatcher("/WEB-INF/login.jsp").forward(request, response);
        }
        
        
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String checkbox = request.getParameter("remember");
        
        User myUser = new User(username, password);
        UserService user = new UserService();
        
        if(username==null || password==null)
        {
            getServletContext().getRequestDispatcher("/WEB-INF/login.jsp").forward(request, response);
        }
        else if(username.isEmpty()||password.isEmpty())
        {
            request.setAttribute("Message", "Both vales are required!");
            request.setAttribute("user", myUser);
            
            getServletContext().getRequestDispatcher("/WEB-INF/login.jsp").forward(request, response);
        }
        else if(user.login(username, password)==true)
        {   
            HttpSession session = request.getSession();
            session.setAttribute("user", myUser);
            
            if(checkbox==null)
            {
                Cookie[] cookies = request.getCookies();
                for(Cookie cookie: cookies)
                {
                    cookie.setMaxAge(0);
                    cookie.setPath("/");
                    cookie.setValue(null);
                    
                    response.addCookie(cookie);
                }
            }
            else
            {
                Cookie cookie = new Cookie("userCookie", username);
                cookie.setMaxAge(60);
                cookie.setPath("/");
                response.addCookie(cookie);
            }
            
            getServletContext().getRequestDispatcher("/WEB-INF/home.jsp").forward(request, response);
            //response.sendRedirect("home");
        }
        else
        {
            request.setAttribute("user", myUser);
            request.setAttribute("Message", "Invalid username and password");
            getServletContext().getRequestDispatcher("/WEB-INF/login.jsp").forward(request, response);
        } 
    }
}
