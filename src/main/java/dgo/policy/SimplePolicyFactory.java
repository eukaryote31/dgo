package dgo.policy;

public class SimplePolicyFactory implements PolicyFactory {
	private static final SimplePolicy INSTANCE = new SimplePolicy();
	
	@Override
	public PolicyComponent build() {
		return INSTANCE;
	}

}
