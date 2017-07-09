package dgo.rollout;

public class SimpleRolloutFactory implements RolloutFactory {
	private static final SimpleRollout INSTANCE = new SimpleRollout();
	
	@Override
	public RolloutPolicy build() {
		return INSTANCE;
	}

}
