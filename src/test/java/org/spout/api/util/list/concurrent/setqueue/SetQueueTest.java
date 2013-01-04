package org.spout.api.util.list.concurrent.setqueue;

import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Test;
import org.spout.api.util.list.concurrent.setqueue.SetQueue;
import org.spout.api.util.list.concurrent.setqueue.SetQueueElement;
import org.spout.api.util.list.concurrent.setqueue.SetQueueFullException;

public class SetQueueTest {
	
	private final int SET_SIZE = 50;
	private final int OPERATIONS = 300;
	
	public void testFull() {
		SetQueue<Integer> queue = new SetQueue<Integer>(10);
		
		IntegerSetQueueElement[] elements = new IntegerSetQueueElement[10];
		for (int i = 0; i < 20; i++) {
			elements[i] = new IntegerSetQueueElement(queue, i);
		}
		
		for (int i = 0; i < 10; i++) {
			elements[i].add();
		}
		
		for (int i = 10; i < 20; i++) {
			verifyFullAdding(elements, i);
		}
	}
	
	@Test
	public void testInvalid() {
		
		SetQueue<Integer> queue = new SetQueue<Integer>(5);
		
		IntegerSetQueueElement[] elements = new IntegerSetQueueElement[10];
		for (int i = 0; i < 10; i++) {
			elements[i] = new IntegerSetQueueElement(queue, i);
		}
		
		for (int i = 0; i < 5; i++) {
			elements[i].add();
		}
		
		verifyFullAdding(elements, 5);
		
		elements[3].setInvalid();
		
		elements[5].add();
		
		for (int i = 0; i < 5; i++) {
			elements[i].setInvalid();
		}
		
		for (int j = 0; j < 10; j++) {
			for (int i = 5; i < 10; i++) {
				elements[i].add();
			}
		}
		
		elements[8].setInvalid();
		
		HashSet<Integer> set = new HashSet<Integer>();
		
		Integer i;
		while ((i = queue.poll()) != null) {
			assertTrue("Element in queue out of range, " + i, i >= 5 && i < 10 && i != 8);
			set.add(i);
		}
		
		assertTrue("Elements missing from queue", set.size() == 4);
		
	}
	
	@Test
	public void testQueueAsSet() {
		
		SetQueue<Integer> queue = new SetQueue<Integer>(SET_SIZE);
		
		IntegerSetQueueElement[] elements = new IntegerSetQueueElement[SET_SIZE];
		for (int i = 0; i < SET_SIZE; i++) {
			elements[i] = new IntegerSetQueueElement(queue, i);
		}

		HashSet<Integer> queued = new HashSet<Integer>();
		
		Random r = new Random();
		
		for (int c = 0; c < OPERATIONS; c++) {
			if (r.nextInt(5) == 0) {
				Integer i = queue.poll();
				if (i == null) {
					assertTrue("Unable to read element from non-empty queue", queued.size() == 0);
				} else {
					assertTrue("Unknown element removed from queue " + i, queued.remove(i));
				}
			} else {
				int i = (r.nextInt() & 0x7FFFFFFF) % SET_SIZE;
				queued.add(i);
				elements[i].add();
			}
		}
		
		Integer i;
		while ((i = queue.poll()) != null) {
			assertTrue("Unknown element removed from queue " + i, queued.remove(i));
		}
		
		assertTrue("All elements not removed from queue, " + queued.size() + " elements remaining", queued.size() == 0);

	}
	
	private static void verifyFullAdding(SetQueueElement<Integer>[] elements, int i) {
		boolean thrown = false;
		try {
			elements[i].add();
		} catch (SetQueueFullException f) {
			thrown = true;
		}
		assertTrue("DirtyQueueFullException was not thrown", thrown);
	}
	
	private static class IntegerSetQueueElement extends SetQueueElement<Integer> {

		private AtomicBoolean valid = new AtomicBoolean(true);
		
		public IntegerSetQueueElement(SetQueue<Integer> queue, Integer value) {
			super(queue, value);
			
		}
		
		public void setInvalid() {
			this.valid.set(false);
		}

		@Override
		protected boolean isValid() {
			return valid.get();
		}
		
	}

}
