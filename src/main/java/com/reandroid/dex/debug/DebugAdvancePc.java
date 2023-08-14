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
package com.reandroid.dex.debug;

import com.reandroid.dex.base.Ule128Item;

public class DebugAdvancePc extends DebugElement{
    private final Ule128Item addressDiff;
    public DebugAdvancePc() {
        super(1, DebugElementType.ADVANCE_PC);
        this.addressDiff = new Ule128Item();
        addChild(1, addressDiff);
    }

    public int getAddressDiff() {
        return addressDiff.get();
    }
    public void setAddressDiff(int addressDiff){
        this.addressDiff.set(addressDiff);
    }

    @Override
    public String toString() {
        return "addressDiff=" + addressDiff;
    }
}
