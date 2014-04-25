/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import dao.ClientContrDAO;
import dao.ServiceInSimDAO;
import dao.ServiceInTariffDao;
import dao.TariffDao;
import filters.TariffFilter;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import objects.ClientContr;
import objects.ServiceInSim;
import objects.ServiceInTariff;
import objects.Tariff;
import objects.User;
import pack.DaoMaster;
import pack.HTMLHelper;
import security.SecurityBean;
import static pack.PathConstants.*;
import static pack.LogManager.LOG;

/**
 *
 * @author Ivan
 */
@WebServlet(name = "TariffServlet", loadOnStartup = 1, urlPatterns = {
    SELECT_ALL_TARIFF,
    TARIFF_FILTER,
    SHOW_TARIFF,
    REMOVE_SERVICE_FROM_TARIFF,
    TARIFF_ADD_FORM,
    TARIFF_ADD
})
public class TariffServlet extends HttpServlet {

    private final TariffDao tariffDao = DaoMaster.getTariffDao();
    private final ServiceInTariffDao servInTarDao = DaoMaster.getServiceInTariffDao();
    private final ClientContrDAO clientContrDao = DaoMaster.getClientContrDao();
    private final ServiceInSimDAO sisDao = DaoMaster.getServiceInSimDao();

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
            case SELECT_ALL_TARIFF: {
                selectAllTariff(request, response);
                break;
            }
            case TARIFF_FILTER: {
                tariffFilter(request, response);
                break;
            }
            case SHOW_TARIFF: {
                showTariff(request, response);
                break;
            }
            case TARIFF_ADD_FORM: {
                tariffAddForm(request, response);
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
        String userPath = request.getServletPath();
        switch (userPath) {
            case REMOVE_SERVICE_FROM_TARIFF: {
                removeServiceFromTariff(request, response);
                break;
            }
        }
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

    private void selectAllTariff(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Tariff> tariffList = tariffDao.getAllTariffList();
        goToSelect(tariffList, request, response);
    }

    /**
     * Перенаправляет на страницу показа списка тарифов.
     *
     * @param tariffList список, который нужно отобразить
     * @param request берём из методов doGet/doPost
     * @param response берём из методов doGet/doPost
     * @throws ServletException
     * @throws IOException
     */
    private void goToSelect(List<Tariff> tariffList, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("TariffList", tariffList);
        request.getRequestDispatcher("/WEB-INF/tariff/showTariffList.jsp").forward(request, response);
    }

    private void removeServiceFromTariff(HttpServletRequest request, HttpServletResponse response) throws IOException {
        SecurityBean.checkAccept(HTMLHelper.getUser(request));
        int idService = Integer.parseInt(request.getParameter("ID_service"));
        int idTariff = Integer.parseInt(request.getParameter("ID_tariff"));
        ServiceInTariff sInT = new ServiceInTariff();
        sInT.setIdService(idService);
        sInT.setIdTariff(idTariff);
        servInTarDao.deleteConcreteServiceInTariff(sInT);
        response.sendRedirect(request.getContextPath() + SHOW_TARIFF + "?ID_tariff=" + idTariff);
    }

    /**
     * Ищет тарифы согласно критерию фильтра и перенаправляет на страинцу вывода
     * тарифов.
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    private void tariffFilter(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        TariffFilter filter = new TariffFilter();
        String nameTariff = request.getParameter("name_tariff");
        String description = request.getParameter("description");
        filter.setNameTariff(nameTariff);
        filter.setDescription(description);

        List<Tariff> tariffList = tariffDao.getFilteredTariffList(filter);
        goToSelect(tariffList, request, response);
    }

    /**
     * Загружает данные о тарифе и направляет на страницу с тарифом. Если
     * просматривается тариф для конкретной сим-карты и юзер имеет право
     * просмотреть эту сим-карту, то также загружаются услуги, подключенные к
     * сим-карте.
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    private void showTariff(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int idTariff = Integer.parseInt(request.getParameter("ID_tariff"));
        List<ServiceInTariff> servInTarList = servInTarDao.getIdTariff(idTariff);
        request.setAttribute("servInTarList", servInTarList);
        String stringSimId = request.getParameter("sim_id");
        User user = HTMLHelper.getUser(request);

        List<ServiceInSim> sisList = null; // Список услуг, подключенных к сим-карте

        // Проверяем, имеет ли юзер право смотреть услуги для этой сим-карты
        if (stringSimId != null && user != null) {
            int simID = Integer.parseInt(stringSimId);
            boolean accept = isClientAcceptedForSim(user, simID);
            if (accept) { // Если есть право смотреть сим-карту, то загружаем
                // подключенные к сим-карте услуги.
                sisList = sisDao.getIdSim(simID);
            }
        } else if (user == null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Юзер отсутствует в сессии.");
            }
        }
        request.setAttribute("sisList", sisList);

        request.getRequestDispatcher("/WEB-INF/tariff/showTariff.jsp").forward(request, response);
    }

    /**
     * Проверяет, имеет ли право пользоватеь смотреть информацию о сим-карте.
     *
     * @param user пользоватеь
     * @param simId ИД сим-карты
     * @return true, если есть право просмотра, иначе false
     */
    private boolean isClientAcceptedForSim(User user, int simId) {
        switch (user.getIdRole()) {
            case SecurityBean.CLIENT: {
                // Проверяем, есть ли среди договоров клиента договор на эту сим-карту.
                for (ClientContr contr : clientContrDao.getContrsByClientID(user.getIdClient())) {
                    if (contr.getSimID() == simId) {
                        return true;
                    }
                }
                return false;
            }
            case SecurityBean.LEGAL_ENTITY: {
                break; // Временно
            }
            case SecurityBean.ADMIN: {
                return true;
            }
        }
        return false;
    }

    /**
     * Перенаправляет на страницу добавления тарифа.
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException 
     */
    private void tariffAddForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/tariff/addTariff.jsp").forward(request, response);
    }
}
