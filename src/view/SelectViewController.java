package view;

import data.Module;
import data.Person;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Callback;

public class SelectViewController extends EqualsView {

	@FXML
	private Label userName;
	
	@FXML
	private ListView<Module> entityList;
	
	@Override
	protected void init() {
		Platform.runLater(() -> {
			Person p = model.getUserLogin().getUser();
			userName.setText(p.getName());
		});
		initEntityListBindings();
	}
	
	/**
	 * Binds the entityList ListView to the module-list property of the EqualsModel,
	 * registers to the change in selection of the items
	 * and automatically selects the first item in the list.
	 */
	private void initEntityListBindings() {
		entityList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE); 
		entityList.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<Module>() {
		    @Override
		    public void onChanged(Change<? extends Module> change) {
		    	System.out.format("selected %s\r\n",entityList.getSelectionModel().getSelectedItem());
		    }
		});
		entityList.setItems(model.getModuleListProperty());
		Platform.runLater(() -> {
			entityList.getSelectionModel().select(0);
		});
		
		
		entityList.setCellFactory(new Callback<ListView<Module>, ListCell<Module>>() {
            @Override
            public ListCell<Module> call(ListView<Module> arg0) {
                return new ListCell<Module>() {

                    @Override
                    protected void updateItem(Module item, boolean bln) {
                		Platform.runLater(() -> {
	                        super.updateItem(item, bln);
	                        if (item != null) {
	                            VBox vBox = new VBox(new Text(item.getShortName()), 
	                            		new Text(String.format("%s $", item.getName())));
	                            HBox hBox = new HBox(new Label("[Graphic]"), vBox);
	                            hBox.setSpacing(10);
	                            setGraphic(hBox);
	                        }
                		});
                    }

                };
            }

        });

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}
}
