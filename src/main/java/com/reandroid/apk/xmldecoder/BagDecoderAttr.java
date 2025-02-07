/*
 *  Copyright (C) 2022 github.com/REAndroid
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.reandroid.apk.xmldecoder;

import com.reandroid.apk.XmlHelper;
import com.reandroid.arsc.array.CompoundItemArray;
import com.reandroid.arsc.util.HexUtil;
import com.reandroid.arsc.value.*;
import com.reandroid.arsc.value.attribute.AttributeBag;
import com.reandroid.arsc.value.attribute.AttributeBagItem;
import com.reandroid.common.EntryStore;

import java.io.IOException;

class BagDecoderAttr<OUTPUT> extends BagDecoder<OUTPUT>{
    public BagDecoderAttr(EntryStore entryStore){
        super(entryStore);
    }

    @Override
    public OUTPUT decode(ResTableMapEntry mapEntry, EntryWriter<OUTPUT> writer) throws IOException {
        Entry entry = mapEntry.getParentEntry();
        String tag = XmlHelper.toXMLTagName(entry.getTypeName());
        writer.enableIndent(true);
        writer.startTag(tag);
        writer.attribute("name", entry.getName());
        AttributeBag attributeBag = AttributeBag.create(mapEntry.getValue());
        writeParentAttributes(writer, mapEntry.getValue());
        ResValueMap formatsMap = mapEntry.getByType(AttributeType.FORMATS);

        boolean is_flag = AttributeTypeFormat.FLAG.matches(formatsMap.getData());

        String childTag = is_flag ? "flag" : "enum";

        AttributeBagItem[] bagItems = attributeBag.getBagItems();

        EntryStore entryStore = getEntryStore();

        for(int i = 0; i < bagItems.length; i++){
            AttributeBagItem item = bagItems[i];
            if(item.isType()){
                continue;
            }
            writer.enableIndent(true);
            writer.startTag(childTag);

            String name = item.getNameOrHex(entryStore);
            writer.attribute("name", name);
            int rawVal = item.getData();
            String value;
            if(item.getBagItem().getValueType() == ValueType.INT_HEX){
                value = HexUtil.toHex8(rawVal);
            }else {
                value = Integer.toString(rawVal);
            }
            writer.text(value);

            writer.endTag(childTag);
        }
        return writer.endTag(tag);
    }

    private void writeParentAttributes(EntryWriter<OUTPUT> writer, CompoundItemArray<? extends ResValueMap> itemArray) throws IOException {
        String formats =  AttributeTypeFormat.toString(itemArray.getFormats());
        if(formats!=null){
            writer.attribute(AttributeType.FORMATS.getName(), formats);
        }
        ResValueMap item = itemArray.getByType(AttributeType.MIN);
        if(item != null){
            writer.attribute(AttributeType.MIN.getName(), Integer.toString(item.getData()));
        }
        item = itemArray.getByType(AttributeType.MAX);
        if(item != null){
            writer.attribute(AttributeType.MAX.getName(), Integer.toString(item.getData()));
        }
        item = itemArray.getByType(AttributeType.L10N);;
        if(item != null){
            writer.attribute(AttributeType.L10N.getName(), Integer.toString(item.getData()));
        }
    }
    private void writeParentAttributes(EntryWriter<OUTPUT> writer, AttributeBag attributeBag) throws IOException {
        String formats=  attributeBag.decodeValueType();
        if(formats!=null){
            writer.attribute("formats", formats);
        }
        AttributeBagItem item = attributeBag.getMin();
        if(item != null){
            writer.attribute("min", item.getBound().toString());
        }
        item = attributeBag.getMax();
        if(item!=null){
            writer.attribute("max", item.getBound().toString());
        }
        item = attributeBag.getL10N();
        if(item!=null){
            writer.attribute("l10n", item.getBound().toString());
        }
    }
    @Override
    public boolean canDecode(ResTableMapEntry mapEntry) {
        return AttributeBag.isAttribute(mapEntry);
    }
}
