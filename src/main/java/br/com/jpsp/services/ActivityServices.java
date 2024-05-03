package br.com.jpsp.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import br.com.jpsp.dao.ActivityDAO;
import br.com.jpsp.model.Activity;
import br.com.jpsp.utils.Utils;

public class ActivityServices implements CRUDServices<Activity> {
	
	public static final ActivityServices instance = new ActivityServices();
	
	private final ActivityDAO dao = ActivityDAO.instance;
	
	private ActivityServices() {
		super();
	}
	
	@Override
	public Set<Activity> getAll() {
		return dao.getAllActivities();
	}

	@Override
	public void add(Activity activity) {
		this.dao.addActivity(activity);
	}

	@Override
	public void remove(Activity activity) throws Exception {
		this.dao.removeActivity(activity);
	}

	@Override
	public void update(Activity activity) throws Exception {
		this.dao.updateActivity(activity);
	}
	
	public List<String> getAllActivitiesDescriptions() {
		Set<Activity> allActivities = this.getAll();
		List<String> allActivitiesDescriptions = new ArrayList<String>();
		if (!Utils.isEmpty(allActivities)) {
			for (Activity a : allActivities) {
				allActivitiesDescriptions.add(a.getDescription());
			}
		}
		return allActivitiesDescriptions;
	}

}
