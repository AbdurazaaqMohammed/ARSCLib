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
package com.reandroid.dex.instruction;

import com.reandroid.dex.base.Ule128Item;
import com.reandroid.dex.index.TypeId;
import com.reandroid.dex.sections.SectionType;

public class TryHandler extends ExceptionHandler {
    private final Ule128Item typeId;
    public TryHandler() {
        super(1);
        this.typeId = new Ule128Item();
        addChild(0, typeId);
    }
    public TypeId getTypeId(){
        return get(SectionType.TYPE_ID, typeId.get());
    }

    @Override
    public String toString() {
        return "try_start_"  + ": " + getTypeId();
    }
}