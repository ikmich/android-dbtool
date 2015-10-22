package org.ikmich.android.dbtools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Represents a list of Record objects.
 * 
 * @author Ikenna Agbasimalo
 * 
 */
public class RecordSet implements List<Record> {

	private List<Record> _recordSet;

	public RecordSet() {
		_recordSet = new ArrayList<Record>();
	}

	@Override
	public boolean add(Record arg0) {
		return _recordSet.add(arg0);
	}

	@Override
	public void add(int arg0, Record arg1) {
		_recordSet.add(arg0, arg1);
	}

	@Override
	public boolean addAll(Collection<? extends Record> arg0) {
		return _recordSet.addAll(arg0);
	}

	@Override
	public boolean addAll(int arg0, Collection<? extends Record> arg1) {
		return _recordSet.addAll(arg0, arg1);
	}

	@Override
	public void clear() {
		_recordSet.clear();
	}

	@Override
	public boolean contains(Object arg0) {
		return _recordSet.contains(arg0);
	}

	@Override
	public boolean containsAll(Collection<?> arg0) {
		return _recordSet.containsAll(arg0);
	}

	@Override
	public Record get(int arg0) {
		return _recordSet.get(arg0);
	}

	@Override
	public int indexOf(Object arg0) {
		return _recordSet.indexOf(arg0);
	}

	@Override
	public boolean isEmpty() {
		return _recordSet.isEmpty();
	}

	@Override
	public Iterator<Record> iterator() {
		return _recordSet.iterator();
	}

	@Override
	public int lastIndexOf(Object arg0) {
		return _recordSet.lastIndexOf(arg0);
	}

	@Override
	public ListIterator<Record> listIterator() {
		return _recordSet.listIterator();
	}

	@Override
	public ListIterator<Record> listIterator(int arg0) {
		return _recordSet.listIterator(arg0);
	}

	@Override
	public boolean remove(Object arg0) {
		return _recordSet.remove(arg0);
	}

	@Override
	public Record remove(int arg0) {
		return _recordSet.remove(arg0);
	}

	@Override
	public boolean removeAll(Collection<?> arg0) {
		return _recordSet.removeAll(arg0);
	}

	@Override
	public boolean retainAll(Collection<?> arg0) {
		return _recordSet.retainAll(arg0);
	}

	@Override
	public Record set(int arg0, Record arg1) {
		return _recordSet.set(arg0, arg1);
	}

	@Override
	public int size() {
		return _recordSet.size();
	}

	@Override
	public List<Record> subList(int arg0, int arg1) {
		return _recordSet.subList(arg0, arg1);
	}

	@Override
	public Object[] toArray() {
		return _recordSet.toArray();
	}

	@Override
	public <T> T[] toArray(T[] arg0) {
		return _recordSet.toArray(arg0);
	}

}
