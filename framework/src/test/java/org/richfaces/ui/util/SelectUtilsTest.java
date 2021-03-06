/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.richfaces.ui.util;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

import org.jboss.test.faces.AbstractFacesTest;
import org.junit.Assert;
import org.richfaces.ui.select.SelectUtils;

import javax.faces.component.UIParameter;
import javax.faces.component.UISelectItem;
import javax.faces.component.UISelectOne;
import javax.faces.model.SelectItem;

import java.util.Iterator;
import java.util.List;

/**
 * @author Gleb Galkin
 * @since 27.01.11
 */
public class SelectUtilsTest extends AbstractFacesTest {
    @Override
    public void setUp() throws Exception {
        super.setUp();
        setupFacesRequest();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    private static <T> List<T> asList(Iterator<T> itr) {
        List<T> result = Lists.newArrayList();
        Iterators.addAll(result, itr);
        return result;
    }

    /**
     * The aim of this test is to check first part of {@link org.richfaces.ui.select.SelectUtils#getSelectItems}, for {@link UISelectOne}
     */
    public void testGetSelectItem() {
        UISelectOne selectOne = new UISelectOne();
        selectOne.getChildren().add(new UISelectItemStub("value1", "label1", "description1", false, false, false));
        selectOne.getChildren().add(new UISelectItemStub("value2", "label2", "description2", false, true, true));

        UISelectItem item = new UISelectItem();
        item.setValue(new SelectItem("value3", "label3", "description3", true, true, true));
        selectOne.getChildren().add(item);

        // non select item at end
        UIParameter param = new UIParameter();
        param.setName("param");
        param.setValue("paramValue");
        selectOne.getChildren().add(param);

        checkSelectItems(asList(SelectUtils.getSelectItems(facesContext, selectOne)));

        // non select item in middle
        selectOne = new UISelectOne();
        selectOne.getChildren().add(new UISelectItemStub("value1", "label1", "description1", false, false, false));
        selectOne.getChildren().add(param);
        selectOne.getChildren().add(new UISelectItemStub("value2", "label2", "description2", false, true, true));
        checkTwoSelectItems(asList(SelectUtils.getSelectItems(facesContext, selectOne)));

        // non select item as value cause IllegalArgumentException
        item = new UISelectItem();
        item.setValue(new UISelectItem());
        selectOne.getChildren().add(item);
        try {
            SelectUtils.getSelectItems(facesContext, selectOne);
        } catch (Exception e) {
            if (!(e instanceof IllegalArgumentException)) {
                Assert.fail("Non select item as value should cause IllegalArgumentException");
            }
        }
    }

    private void checkSelectItems(List<SelectItem> selectItems) {
        checkTwoSelectItems(selectItems);

        Assert.assertNotNull(selectItems.get(2));
        Assert.assertEquals("value3", selectItems.get(2).getValue());
        Assert.assertEquals("label3", selectItems.get(2).getLabel());
        Assert.assertEquals("description3", selectItems.get(2).getDescription());
        Assert.assertEquals(true, selectItems.get(2).isDisabled());
        Assert.assertEquals(true, selectItems.get(2).isEscape());
        Assert.assertEquals(true, selectItems.get(2).isNoSelectionOption());
    }

    private void checkTwoSelectItems(List<SelectItem> selectItems) {
        Assert.assertNotNull(selectItems.get(0));
        Assert.assertEquals("value1", selectItems.get(0).getValue());
        Assert.assertEquals("label1", selectItems.get(0).getLabel());
        Assert.assertEquals("description1", selectItems.get(0).getDescription());
        Assert.assertEquals(false, selectItems.get(0).isDisabled());
        Assert.assertEquals(false, selectItems.get(0).isEscape());
        Assert.assertEquals(false, selectItems.get(0).isNoSelectionOption());

        Assert.assertNotNull(selectItems.get(1));
        Assert.assertEquals("value2", selectItems.get(1).getValue());
        Assert.assertEquals("label2", selectItems.get(1).getLabel());
        Assert.assertEquals("description2", selectItems.get(1).getDescription());
        Assert.assertEquals(false, selectItems.get(1).isDisabled());
        Assert.assertEquals(true, selectItems.get(1).isEscape());
        Assert.assertEquals(true, selectItems.get(1).isNoSelectionOption());
    }

    private class UISelectItemStub extends UISelectItem {
        public UISelectItemStub(Object itemValue, String itemLabel, String itemDescription, boolean itemDisabled,
            boolean itemEscaped, boolean noSelectionOption) {
            super();
            setItemValue(itemValue);
            setItemLabel(itemLabel);
            setItemDescription(itemDescription);
            setItemDisabled(itemDisabled);
            setItemEscaped(itemEscaped);
            setNoSelectionOption(noSelectionOption);
        }
    }
}
