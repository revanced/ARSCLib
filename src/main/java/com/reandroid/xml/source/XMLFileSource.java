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
package com.reandroid.xml.source;

import com.reandroid.xml.XMLDocument;
import com.reandroid.xml.XMLException;

import java.io.File;

public class XMLFileSource implements XMLSource{
    private final String path;
    private final File file;
    public XMLFileSource(String path, File file){
        this.path=path;
        this.file=file;
    }
    public File getFile(){
        return this.file;
    }
    @Override
    public String getPath() {
        return path;
    }
    @Override
    public XMLDocument getXMLDocument() throws XMLException {
        return XMLDocument.load(getFile());
    }
    @Override
    public void disposeXml() {
    }


}
