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
package com.reandroid.dex.ins;

import com.reandroid.dex.writer.SmaliWriter;


public class Ins32x extends Size6Ins {

    public Ins32x(Opcode<?> opcode) {
        super(opcode);
    }

    @Override
    public int getData(){
        return 0;
    }
    public void setData(int data){
    }

    @Override
    public int getRegistersCount() {
        return 2;
    }
    @Override
    public int getRegister(int index) {
        return getShort(2 + index);
    }
    @Override
    public void setRegister(int index, int value) {
        setShort(2 + index, value);
    }
    @Override
    void appendCodeData(SmaliWriter writer) {
    }
}