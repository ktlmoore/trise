package com.tlear.trise.graph;

import java.util.Comparator;

import com.badlogic.gdx.math.Vector2;
import com.tlear.trise.environment.Environment;
import com.tlear.trise.objects.StaticGoal;

public class DistanceToNearestGoalComparator<T> implements Comparator<T> {

		private Environment env;
		
		public DistanceToNearestGoalComparator(Environment env) {
			this.env = env;
		}
		
		@Override
		public int compare(Object o1, Object o2) {
			if (!(o1 instanceof Node<?>) || !(o2 instanceof Node<?>)) {
				throw new IllegalArgumentException("DistanceToNearestGoalComparator must compare two Node<?>s");
			}
			Node<?> n1 = (Node<?>) o1;
			Node<?> n2 = (Node<?>) o2;
			if (!(n1.getValue() instanceof Vector2) || !(n2.getValue() instanceof Vector2)) {
				throw new IllegalArgumentException("DistanceToNearestGoalComparator must compare two nodes containing Vector2");
			}
			
			Vector2 v1 = ((Vector2) n1.getValue()).cpy();
			Vector2 v2 = ((Vector2) n2.getValue()).cpy();
			
			Vector2 s1 = new Vector2(Float.MAX_VALUE, Float.MAX_VALUE);
			Vector2 s2 = new Vector2(Float.MAX_VALUE, Float.MAX_VALUE);
			
			for (StaticGoal g : env.goals) {
				
				Vector2 gpos = g.pos.cpy();
				Vector2 d = gpos.sub(v1.cpy());
				if (d.len2() < s1.len2()) {
					s1 = d.cpy();
				}
				gpos = g.pos.cpy();
				d = gpos.sub(v2.cpy());
				if (d.len2() < s2.len2()) {
					s2 = d.cpy();
				}
				
			}
			
			System.out.println("S1: " + s1);
			System.out.println("S2: " + s2);
			
			return (int) (s1.len() - s2.len());
		}
	}