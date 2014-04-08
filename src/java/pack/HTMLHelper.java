/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pack;

/**
 * Класс для помощи в составлении HTML-страниц.
 * Берёт на себя рутинную работу. Всё, что придётся
 * часто использовать в JSP-шках, желательно спрятать сюда.
 * @author Ivan
 */
public class HTMLHelper {
    
    /**
     * Получаем кнопки Update и Delete
     * @param updatePath путь для update
     * @param deletePath путь для delete
     * @param name имя параметра
     * @param id ID записи, для которой создаются кнопки
     * @return HTML-код, содержащий две кнопки
     */
    public static String makeUpdateAndDelete(String updatePath, String deletePath, String name, int id) {
        String buttons = 
                "<table>"
                 + "<tr>"
                        + "<td>"
                            + "<form name=\"Data Input Form\" action=\"" + updatePath + "\" method=\"POST\">"  
                                + "<input type=\"submit\" value=\"update\" />" 
                                + "<input type = \"hidden\" name = \"" + name + "\" value = \"" + id + "\"/>"
                            + "</form>"
                        + "</td>"
                        + "<td>"
                            + "<form name=\"Data Input Form\" action=\"" + deletePath + "\" method=\"POST\">"  
                                + "<input type=\"submit\" value=\"delete\" />" 
                                + "<input type = \"hidden\" name = \"" + name + "\" value = \"" + id + "\"/>"
                            + "</form>"
                        + "</td>"
                    + "</tr>"
                + "</table>";
        return buttons;    
    }
}