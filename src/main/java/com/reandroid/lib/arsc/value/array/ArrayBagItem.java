package com.reandroid.lib.arsc.value.array;

import com.reandroid.lib.arsc.chunk.PackageBlock;
import com.reandroid.lib.arsc.chunk.TableBlock;
import com.reandroid.lib.arsc.item.SpecString;
import com.reandroid.lib.arsc.item.TableString;
import com.reandroid.lib.arsc.pool.SpecStringPool;
import com.reandroid.lib.arsc.pool.TableStringPool;
import com.reandroid.lib.arsc.value.EntryBlock;
import com.reandroid.lib.arsc.value.ResValueBagItem;
import com.reandroid.lib.arsc.value.ValueType;
import com.reandroid.lib.arsc.value.plurals.PluralsBagItem;
import com.reandroid.lib.arsc.value.plurals.PluralsQuantity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ArrayBagItem {
    private final ResValueBagItem mBagItem;
    public ArrayBagItem(ResValueBagItem bagItem){
        this.mBagItem=bagItem;
    }
    public ResValueBagItem getBagItem() {
        return mBagItem;
    }

    public ValueType getValueType(){
        return getBagItem().getValueType();
    }
    private TableStringPool getStringPool(){
        EntryBlock entryBlock=getBagItem().getEntryBlock();
        if(entryBlock==null){
            return null;
        }
        PackageBlock pkg = entryBlock.getPackageBlock();
        if(pkg==null){
            return null;
        }
        TableBlock tableBlock= pkg.getTableBlock();
        if(tableBlock==null){
            return null;
        }
        return tableBlock.getTableStringPool();
    }
    public int getValue(){
        return getBagItem().getData();
    }
    public boolean hasStringValue(){
        return getValueType()==ValueType.STRING;
    }
    public boolean hasReferenceValue(){
        return getValueType()==ValueType.REFERENCE;
    }
    public String getStringValue(){
        ValueType valueType=getValueType();
        if(valueType!=ValueType.STRING){
            throw new IllegalArgumentException("Not STRING ValueType="+valueType);
        }
        TableStringPool stringPool=getStringPool();
        if(stringPool==null){
            return null;
        }
        int ref=getValue();
        TableString tableString = stringPool.get(ref);
        return tableString.getHtml();
    }

    @Override
    public String toString() {
        StringBuilder builder=new StringBuilder();
        builder.append("<item>");
        if(hasStringValue()){
            builder.append(getStringValue());
        }else {
            builder.append(String.format("0x%08x", getValue()));
        }
        builder.append("</item>");
        return builder.toString();
    }
    public static ArrayBagItem[] create(ResValueBagItem[] resValueBagItems){
        if(resValueBagItems==null){
            return null;
        }
        int len=resValueBagItems.length;
        if(len==0){
            return null;
        }
        List<ArrayBagItem> results=new ArrayList<>();
        for(int i=0;i<len;i++){
            ArrayBagItem item=create(resValueBagItems[i]);
            if(item==null){
                return null;
            }
            results.add(item);
        }
        return results.toArray(new ArrayBagItem[0]);
    }
    public static ArrayBagItem create(ResValueBagItem resValueBagItem){
        if(resValueBagItem==null){
            return null;
        }
        ArrayBagItem item=new ArrayBagItem(resValueBagItem);
        return item;
    }
}