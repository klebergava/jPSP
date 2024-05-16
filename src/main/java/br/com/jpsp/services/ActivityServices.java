package br.com.jpsp.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import br.com.jpsp.model.Activity;
import br.com.jpsp.utils.Utils;

/*8
 *
 */
public class ActivityServices extends RepositoryAccessServices implements CRUDServices<Activity> {
	public static final ActivityServices instance = new ActivityServices();

	private ActivityServices() {
		super();
	}

	@Override
	public Set<Activity> getAll() {
		return activityDAO.getAll();
	}

	@Override
	public void add(Activity activity) {
		synchronized (ActivityServices.this) {
			this.activityDAO.add(activity);
		}
	}

	@Override
	public void remove(Activity activity) throws Exception {
		synchronized (ActivityServices.this) {
			this.activityDAO.remove(activity);
		}
	}

	@Override
	public void update(Activity activity) throws Exception {
		synchronized (ActivityServices.this) {
			this.activityDAO.update(activity);
		}
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

	public void addActivities(Set<String> activities) {
		if (!Utils.isEmpty(activities)) {
			activities.forEach(activity -> {
				Activity newActivity = new Activity().from(activity);
				if (!activityDAO.exists(newActivity)) {
					activityDAO.add(newActivity);
				}
			});
		}
	}
}
