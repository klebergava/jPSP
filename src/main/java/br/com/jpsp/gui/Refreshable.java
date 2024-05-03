package br.com.jpsp.gui;

import br.com.jpsp.model.Task;

public interface Refreshable {
	void refresh();

	void doContinue(Task paramTask);
}
