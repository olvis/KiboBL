/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bo.com.kibo.bl.exceptions;

/**
 *
 * @author Olvinho
 */
public class BusinessExceptionMessage {

    private int index;
    private String message;
    private String property;

    public BusinessExceptionMessage(String message, String property, int index) {
        this.index = index;
        this.message = message;
        this.property = property;
    }

    public BusinessExceptionMessage(String message) {
        this(message, "", -1);
    }

    public BusinessExceptionMessage(String message, String property) {
        this(message, property, -1);
    }

    public int getIndex() {
        return index;
    }

    public String getMessage() {
        return message;
    }

    public String getProperty() {
        return property;
    }

}
