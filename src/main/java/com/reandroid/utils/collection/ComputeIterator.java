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
package com.reandroid.utils.collection;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.Predicate;

public class ComputeIterator<E, T> implements Iterator<T> {
    private final Iterator<? extends E> iterator;
    private final Transformer<? super E, T> transformer;
    private final Predicate<T> filter;
    private T mNext;
    public ComputeIterator(Iterator<? extends E> iterator, Transformer<? super E, T> transformer, Predicate<T> filter){
        this.iterator = iterator;
        this.transformer = transformer;
        this.filter = filter;
    }
    public ComputeIterator(Iterator<? extends E> iterator, Transformer<? super E, T> transformer){
        this(iterator, transformer, null);
    }
    @Override
    public boolean hasNext() {
        return getNext() != null;
    }
    @Override
    public T next() {
        T item = getNext();
        if(item == null){
            throw new NoSuchElementException();
        }
        mNext = null;
        return item;
    }
    private T getNext(){
        if(mNext == null) {
            while (iterator.hasNext()) {
                T output = transformer.transform(iterator.next());
                if (output != null) {
                    if(filter == null || filter.evaluate(output)){
                        mNext = output;
                        break;
                    }
                }
            }
        }
        return mNext;
    }
    public static<E1, T1> Iterator<T1> of(Iterator<? extends E1> iterator, Transformer<? super E1, T1> transformer){
        if(!iterator.hasNext()){
            return EmptyIterator.of();
        }
        return new ComputeIterator<>(iterator, transformer);
    }
}
