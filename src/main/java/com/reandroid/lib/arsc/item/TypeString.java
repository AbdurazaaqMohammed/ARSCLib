package com.reandroid.lib.arsc.item;


public class TypeString extends StringItem {
    public TypeString(boolean utf8) {
        super(utf8);
    }
    public int getId(){
        return getIndex()+1;
    }
    @Override
    StyleItem getStyle(){
        return null;
    }
}