/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import dao.ClientContrDAO;
import dao.PhoneNumberDAO;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import objects.Client;
import objects.ClientContr;
import objects.PhoneNumber;
import objects.User;
import pack.DaoMaster;

/**
 *
 * @author Ivan
 */
@WebServlet(name = "ClientServlet", loadOnStartup = 1, urlPatterns = {
    "/clientHome/",
})
public class ClientServlet extends HttpServlet {

    private ClientContrDAO clientContrDao = DaoMaster.getClientContrDao();
    private PhoneNumberDAO phoneNumberDao = DaoMaster.getPhoneNumberDao();

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String userPath = request.getServletPath();
        switch (userPath) {
            case "/clientHome/": {
                clientHome(request, response);
                break;
            }
        }
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    /**
     * Загружает информацию о клиенте, связанном с данным юзером, и
     * перенаправляет на домашнюю для клиента страницу.
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    private void clientHome(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            // что-нибудь сделать
            return;
        }
        List<ClientContr> contrs = clientContrDao.getContrsByClientID(user.getIdClient());
        if (contrs.isEmpty()) {
            // что-нибудь сделать
            return;
        }
        // Можем взять клиента из первого элмента, так как проверили, что список не пустой.
        Client client = contrs.get(0).getClient();
        session.setAttribute("currentClient", client);
        
        // Соотносим телефонные номера с договорами.
        Map<ClientContr, PhoneNumber> phonesMap = new HashMap<>();
        for (ClientContr contr: contrs) {
            phonesMap.put(contr, phoneNumberDao.getNumberBySimID(contr.getSimID()));
        }
        request.setAttribute("phonesMap", phonesMap); // Кладём список всех контрактов в запрос.
        request.getRequestDispatcher("/WEB-INF/clientHome.jsp").forward(request, response);
    }
}
