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

import com.reandroid.arsc.item.IntegerReference;
import com.reandroid.arsc.item.IntegerVisitor;
import com.reandroid.arsc.item.VisitableInteger;

public class InsConst extends Ins31i implements IntegerReference, VisitableInteger {

    public InsConst() {
        super(Opcode.CONST);
    }

    @Override
    public void visitIntegers(IntegerVisitor visitor) {
        visitor.visit(this, this);
    }

    @Override
    public int get() {
        return getData();
    }
    @Override
    public void set(int value) {
        setData(value);
    }

    @Override
    public int getRegistersCount() {
        return 1;
    }
    @Override
    public int getRegister(int index) {
        return getByteUnsigned(1);
    }
    @Override
    public void setRegister(int index, int value) {
        setByte(1, value);
    }
    @Override
    public int getData() {
        return getInteger(2);
    }
    @Override
    public void setData(int data) {
        setInteger(2, data);
    }
}