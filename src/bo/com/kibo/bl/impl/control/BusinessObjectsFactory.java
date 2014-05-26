/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bo.com.kibo.bl.impl.control;

import bo.com.kibo.bl.impl.AreaBO;
import bo.com.kibo.bl.intf.IAreaBO;

/**
 *
 * @author Olvinho
 */
public class BusinessObjectsFactory {

    private static final ThreadLocal<BusinessObjectsFactory> box = new ThreadLocal<>();

    private BusinessObjectsFactory() {

    }

    public static BusinessObjectsFactory getInstance() {
        BusinessObjectsFactory businessObjectsFactory = box.get();
        if (businessObjectsFactory == null) {
            businessObjectsFactory = new BusinessObjectsFactory();
            box.set(businessObjectsFactory);
        }
        return businessObjectsFactory;
    }

    private IAreaBO areaBO;

    public IAreaBO getAreaBO() {
        if (areaBO == null) {
            areaBO = new AreaBO();
        }
        return areaBO;
    }

}
