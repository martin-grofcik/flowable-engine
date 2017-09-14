/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.flowable.cmmn.converter;

import javax.xml.stream.XMLStreamReader;

import org.apache.commons.lang3.StringUtils;
import org.flowable.cmmn.model.CmmnElement;

/**
 * @author Joram Barrez
 */
public class DocumentationXmlConverter extends BaseCmmnXmlConverter {
    
    @Override
    public String getXMLElementName() {
        return CmmnXmlConstants.ELEMENT_DOCUMENTATION;
    }
    
    @Override
    public boolean isCmmnElement() {
        return false;
    }

    @Override
    protected CmmnElement convert(XMLStreamReader xtr, ConversionHelper conversionHelper) {
        String documentation = xtr.getText();
        if (StringUtils.isNotEmpty(documentation)) {
            conversionHelper.getCurrentCmmnElement().setDocumentation(documentation);
            
            String textFormat = xtr.getAttributeValue(null, CmmnXmlConstants.ATTRIBUTE_TEXT_FORMAT);
            if (StringUtils.isEmpty(textFormat)) {
                textFormat = "text/plain";
            }
            conversionHelper.getCurrentCmmnElement().setDocumentationTextFormat(textFormat);
            
        }
        return null;
    }
    
}