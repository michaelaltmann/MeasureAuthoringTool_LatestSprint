package mat.client.clause.cqlworkspace;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.gwtbootstrap3.client.ui.InlineRadio;
import org.gwtbootstrap3.client.ui.gwt.FlowPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.ycp.cs.dh.acegwt.client.ace.AceAnnotationType;
import edu.ycp.cs.dh.acegwt.client.ace.AceCommand;
import edu.ycp.cs.dh.acegwt.client.ace.AceEditor;
import mat.client.CqlComposerPresenter;
import mat.client.Mat;
import mat.client.MatPresenter;
import mat.client.clause.QDSAttributesService;
import mat.client.clause.QDSAttributesServiceAsync;
import mat.client.clause.cqlworkspace.CQLFunctionsView.Observer;
import mat.client.clause.event.QDSElementCreatedEvent;
import mat.client.codelist.HasListBox;
import mat.client.codelist.service.SaveUpdateCodeListResult;
import mat.client.event.CQLLibrarySelectedEvent;
import mat.client.measure.service.CQLLibraryServiceAsync;
import mat.client.measure.service.SaveCQLLibraryResult;
import mat.client.shared.CQLButtonToolBar;
import mat.client.shared.MatContext;
import mat.client.shared.ValueSetNameInputValidator;
import mat.client.umls.service.VSACAPIServiceAsync;
import mat.client.umls.service.VsacApiResult;
import mat.model.CQLValueSetTransferObject;
import mat.model.CodeListSearchDTO;
import mat.model.MatValueSet;
import mat.model.VSACExpansionProfile;
import mat.model.VSACVersion;
import mat.model.clause.QDSAttributes;
import mat.model.cql.CQLDefinition;
import mat.model.cql.CQLFunctionArgument;
import mat.model.cql.CQLFunctions;
import mat.model.cql.CQLIncludeLibrary;
import mat.model.cql.CQLLibraryDataSetObject;
import mat.model.cql.CQLParameter;
import mat.model.cql.CQLQualityDataSetDTO;
import mat.shared.CQLErrors;
import mat.shared.CQLModelValidator;
import mat.shared.ConstantMessages;
import mat.shared.GetUsedCQLArtifactsResult;
import mat.shared.SaveUpdateCQLResult;

public class CQLStandaloneWorkSpacePresenter implements MatPresenter{

	/** The panel. */
	private SimplePanel panel = new SimplePanel();
	
	/** The search display. */
	private ViewDisplay searchDisplay;
	
	/** The empty widget. */
	private SimplePanel emptyWidget = new SimplePanel();
	
	/** The is measure details loaded. */
	private boolean isCQLWorkSpaceLoaded = false;
	
	private String currentSection = "general";
	/** The next clicked menu. */
	private String nextSection = "general";
	
	/** The applied QDM list. */
	private List<CQLQualityDataSetDTO> appliedValueSetTableList = new ArrayList<CQLQualityDataSetDTO>();
	
	CQLModelValidator validator = new CQLModelValidator();
	
	private String cqlLibraryName;
	
	/** The exp profile to all qdm. */
	private String expProfileToAllValueSet = "";

	/** The is modfied. */
	private boolean isModified = false;

	/** The is expansion profile. */
	private boolean isExpansionProfile = false;
	
	/** The is u ser defined. */
	private boolean isUserDefined = false;
	
	/** The modify value set dto. */
	private CQLQualityDataSetDTO modifyValueSetDTO;
	
	/** QDSAttributesServiceAsync instance. */
	private QDSAttributesServiceAsync attributeService = (QDSAttributesServiceAsync) GWT
			.create(QDSAttributesService.class);
	
	private AceEditor curAceEditor;
	
	/** The vsacapi service. */
	private final VSACAPIServiceAsync vsacapiService = MatContext.get().getVsacapiServiceAsync();
	
	/** The cql service. */
	private final CQLLibraryServiceAsync cqlService = MatContext.get().getCQLLibraryService();
	
	/** The current mat value set. */
	private MatValueSet currentMatValueSet;
	
	/**
	 * The Interface ViewDisplay.
	 */
	public static interface ViewDisplay{


		/**
		 * Top Main panel of CQL Workspace Tab.
		 * 
		 * @return HorizontalPanel
		 */
		VerticalPanel getMainPanel();
		
		/**
		 * Gets the main v panel.
		 *
		 * @return the main v panel
		 */
		Widget asWidget();
		
		/**
		 * Gets the main h panel.
		 *
		 * @return the main h panel
		 */
		HorizontalPanel getMainHPanel();
		
		/**
		 * Gets the main flow panel.
		 *
		 * @return the main flow panel
		 */
		FlowPanel getMainFlowPanel();

		/**
		 * Generates View for CQLWorkSpace tab.
		 */
		void buildView();

		String getClickedMenu();

		void setClickedMenu(String clickedMenu);

		String getNextClickedMenu();

		void setNextClickedMenu(String nextClickedMenu);

		CQLLeftNavBarPanelView getCqlLeftNavBarPanelView();

		void resetMessageDisplay();

		void hideAceEditorAutoCompletePopUp();

		CQLParametersView getCQLParametersView();

		CQlDefinitionsView getCQLDefinitionsView();

		CQLFunctionsView getCQLFunctionsView();

		CQLIncludeLibraryView getCqlIncludeLibraryView();

		void buildCQLFileView();

		AceEditor getCqlAceEditor();

		void buildGeneralInformation();

		CQLGeneralInformationView getCqlGeneralInformationView();

		CQLIncludeLibraryView getIncludeView();

		TextBox getAliasNameTxtArea();

		AceEditor getViewCQLEditor();

		TextBox getOwnerNameTextBox();

		void buildIncludesView();

		void resetAll();

		void buildParameterLibraryView();

		void buildDefinitionLibraryView();

		void buildFunctionLibraryView();

		void createAddArgumentViewForFunctions(List<CQLFunctionArgument> argumentList);

		CQLButtonToolBar getParameterButtonBar();

		CQLButtonToolBar getDefineButtonBar();

		CQLButtonToolBar getFunctionButtonBar();

		/*void buildInfoPanel(Widget sourceWidget);*/

		TextBox getDefineNameTxtArea();

		AceEditor getDefineAceEditor();

		InlineRadio getContextDefinePATRadioBtn();

		InlineRadio getContextDefinePOPRadioBtn();

		TextBox getFuncNameTxtArea();

		AceEditor getFunctionBodyAceEditor();

		InlineRadio getContextFuncPATRadioBtn();

		InlineRadio getContextFuncPOPRadioBtn();

		List<CQLFunctionArgument> getFunctionArgumentList();

		TextBox getParameterNameTxtArea();

		AceEditor getParameterAceEditor();

		Map<String, CQLFunctionArgument> getFunctionArgNameMap();

		void createAddArgumentViewForFunctions(List<CQLFunctionArgument> argumentList, boolean isEditable);

		CQLAppliedValueSetView getValueSetView();

		void buildAppliedQDM();

	}
	
	/**
	 * Instantiates a new CQL presenter
	 *
	 */
	public CQLStandaloneWorkSpacePresenter(final ViewDisplay srchDisplay) {
		searchDisplay = srchDisplay;
		emptyWidget.add(new Label("No CQL Library Selected"));
		addEventHandlers();
	}
	
	private void addEventHandlers() {
		MatContext.get().getEventBus().addHandler(CQLLibrarySelectedEvent.TYPE, new CQLLibrarySelectedEvent.Handler() {

			@Override
			public void onLibrarySelected(CQLLibrarySelectedEvent event) {
				isCQLWorkSpaceLoaded = false;
				if (event.getCqlLibraryId() != null) {
					isCQLWorkSpaceLoaded = true;
					logRecentActivity();
				} else {
					displayEmpty();
				}
			}

		});
		searchDisplay.getCqlLeftNavBarPanelView().getDeleteConfirmationDialogBoxYesButton().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (searchDisplay.getCqlLeftNavBarPanelView().getCurrentSelectedDefinitionObjId() != null) {
					deleteDefinition();
					searchDisplay.getCqlLeftNavBarPanelView().getDeleteConfirmationDialogBox().hide();
				} else if (searchDisplay.getCqlLeftNavBarPanelView().getCurrentSelectedFunctionObjId() != null) {
					deleteFunction();
					searchDisplay.getCqlLeftNavBarPanelView().getDeleteConfirmationDialogBox().hide();
				} else if (searchDisplay.getCqlLeftNavBarPanelView().getCurrentSelectedParamerterObjId() != null) {
					deleteParameter();
					searchDisplay.getCqlLeftNavBarPanelView().getDeleteConfirmationDialogBox().hide();
				} else if(searchDisplay.getCqlLeftNavBarPanelView().getCurrentSelectedIncLibraryObjId() != null){
					deleteInclude();
					searchDisplay.getCqlLeftNavBarPanelView().getDeleteConfirmationDialogBox().hide();
				}
			}
		});
		
		searchDisplay.getCqlLeftNavBarPanelView().getDeleteConfirmationDialogBoxNoButton().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				searchDisplay.resetMessageDisplay();
				searchDisplay.getCqlLeftNavBarPanelView().getDeleteConfirmationDialogBox().hide();
			}
		});
		
		ClickHandler cHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				searchDisplay.getCQLDefinitionsView().getDefineAceEditor().detach();
				searchDisplay.getCQLParametersView().getParameterAceEditor().detach();
				searchDisplay.getCQLFunctionsView().getFunctionBodyAceEditor().detach();
			}
		};
		searchDisplay.getMainPanel().addDomHandler(cHandler, ClickEvent.getType());


		addGeneralInfoEventHandlers();
		addValueSetEventHandlers();
		addIncludeCQLLibraryHandlers();
		addParameterEventHandlers();
		addDefineEventHandlers();
		addFunctionEventHandlers();
		addEventHandlerOnAceEditors();
		addEventHandlersOnContextRadioButtons();
		addWarningAlertHandlers();
	}


	/**
	 * On modify value set qdm.
	 *
	 * @param result the result
	 * @param isUserDefined the is user defined
	 */
	private void onModifyValueSet(CQLQualityDataSetDTO result, boolean isUserDefined){

		String oid = isUserDefined ? "" : result.getOid();
		searchDisplay.getValueSetView().getOIDInput().setEnabled(true);

		searchDisplay.getValueSetView().getOIDInput().setValue(oid);
		searchDisplay.getValueSetView().getOIDInput().setTitle(oid);

		searchDisplay.getValueSetView().getRetrieveFromVSACButton().setEnabled(!isUserDefined);

		searchDisplay.getValueSetView().getUserDefinedInput().setEnabled(isUserDefined);
		searchDisplay.getValueSetView().getUserDefinedInput().setValue(result.getCodeListName());
		searchDisplay.getValueSetView().getUserDefinedInput().setTitle(result.getCodeListName());

		searchDisplay.getValueSetView().getQDMExpProfileListBox().clear();
		if(result.getExpansionIdentifier() != null){
			if(!isUserDefined)
				searchDisplay.getValueSetView().getQDMExpProfileListBox().addItem(result.getExpansionIdentifier(),result.getExpansionIdentifier());
		}
		searchDisplay.getValueSetView().getQDMExpProfileListBox().setEnabled(false);

		searchDisplay.getValueSetView().getVersionListBox().clear();
		searchDisplay.getValueSetView().getVersionListBox().setEnabled(false);

		expProfileToAllValueSet = getExpProfileValue();

		if(!expProfileToAllValueSet.isEmpty()){
			searchDisplay.getValueSetView().getQDMExpProfileListBox().clear();
			if(!isUserDefined)
				searchDisplay.getValueSetView().getQDMExpProfileListBox().addItem(expProfileToAllValueSet,
						expProfileToAllValueSet);
		}

		searchDisplay.getValueSetView().getSaveButton().setEnabled(isUserDefined);
	}
	
	private String getExpProfileValue() {
		int selectedindex =	searchDisplay.getValueSetView().getVSACExpansionProfileListBox().getSelectedIndex();
		String result = searchDisplay.getValueSetView().getVSACExpansionProfileListBox().getValue(selectedindex);
		if (!result.equalsIgnoreCase(MatContext.PLEASE_SELECT)){
			return result;
		}else{
			return "";
		}
	}
	
	/**
	 * Save library xml.
	 *
	 * @param toBeDeletedValueSetId the to Be Deleted Value Set Id
	
	 */
	private void deleteValueSet(String toBeDeletedValueSetId) {
		showSearchingBusy(true);
		MatContext.get().getCQLLibraryService().deleteValueSet(toBeDeletedValueSetId,  MatContext.get()
				.getCurrentCQLLibraryId(),  new AsyncCallback<SaveUpdateCQLResult>() {
			
			@Override
			public void onFailure(final Throwable caught) {
				Window.alert(MatContext.get().getMessageDelegate()
						.getGenericErrorMessage());
				showSearchingBusy(false);
			}
			
			@Override
			public void onSuccess(final SaveUpdateCQLResult result) {
				if(result != null && result.getCqlErrors().isEmpty()){
					modifyValueSetDTO = null;
					//The below call will update the Applied QDM drop down list in insert popup.
					getAppliedValueSetList();
					searchDisplay.getCqlLeftNavBarPanelView().getSuccessMessageAlert().createAlert(
							MatContext.get().getMessageDelegate().getSUCCESSFUL_QDM_REMOVE_MSG());
					searchDisplay.getCqlLeftNavBarPanelView().getSuccessMessageAlert().setVisible(true);
				}
				showSearchingBusy(false);
			}
		});
	}
	
	/**
	 * Gets the applied QDM list.
	 *
	 * @return the applied QDM list
	 */
	private void getAppliedValueSetList() {
		showSearchingBusy(true);
		String cqlLibraryId = MatContext.get().getCurrentCQLLibraryId();
		if ((cqlLibraryId != null) && !cqlLibraryId.equals("")) {
			MatContext.get().getLibraryService().getCQLData(cqlLibraryId, new AsyncCallback<SaveUpdateCQLResult>() {

				@Override
				public void onFailure(Throwable caught) {
					Window.alert(MatContext.get().getMessageDelegate()
							.getGenericErrorMessage());
					showSearchingBusy(false);
					
				}

				@Override
				public void onSuccess(SaveUpdateCQLResult result) {
					String ExpIdentifier = null;
					appliedValueSetTableList.clear();
					
					List<CQLQualityDataSetDTO> allValuesets = new ArrayList<CQLQualityDataSetDTO>();				
					
					for (CQLQualityDataSetDTO dto : result.getCqlModel().getAllValueSetList()) {
						if(dto.isSuppDataElement())
							continue;
						allValuesets.add(dto);
						if(dto.getVsacExpIdentifier() != null){
							if(!dto.getVsacExpIdentifier().isEmpty() && !dto.getVsacExpIdentifier().equalsIgnoreCase("")){
								ExpIdentifier = dto.getVsacExpIdentifier();
							}
						}
					}
					
					searchDisplay.getCqlLeftNavBarPanelView().setAppliedQdmList(allValuesets);
					MatContext.get().setValuesets(allValuesets);
					for(CQLQualityDataSetDTO valueset : allValuesets){
						//filtering out codes from valuesets list
						if (valueset.getOid().equals("419099009") || valueset.getOid().equals("21112-8")) 
							continue;
								
						appliedValueSetTableList.add(valueset);
					}
					
					
					searchDisplay.getValueSetView().buildAppliedValueSetCellTable(appliedValueSetTableList, MatContext.get().getLibraryLockService()
							.checkForEditPermission());
					//if UMLS is not logged in
					if (!MatContext.get().isUMLSLoggedIn()) {
						if(ExpIdentifier!=null){
							searchDisplay.getValueSetView().getVSACExpansionProfileListBox().setEnabled(false);
							searchDisplay.getValueSetView().getVSACExpansionProfileListBox().clear();
							searchDisplay.getValueSetView().getVSACExpansionProfileListBox().addItem(result.getCqlQualityDataSetDTO().getVsacExpIdentifier());
							searchDisplay.getValueSetView().getDefaultExpProfileSel().setValue(true);
							searchDisplay.getValueSetView().getDefaultExpProfileSel().setEnabled(false);
							isExpansionProfile = true;
							expProfileToAllValueSet = result.getCqlQualityDataSetDTO().getVsacExpIdentifier();
						} else {
							expProfileToAllValueSet = "";
							isExpansionProfile = false;
						}
					} else {
						if(ExpIdentifier!=null){
							searchDisplay.getValueSetView().getVSACExpansionProfileListBox().setEnabled(true);
							searchDisplay.getValueSetView().setExpProfileList(MatContext.get()
									.getExpProfileList());
							searchDisplay.getValueSetView().setDefaultExpansionProfileListBox();
							for(int i = 0; i < searchDisplay.getValueSetView().getVSACExpansionProfileListBox().getItemCount(); i++){
								if(searchDisplay.getValueSetView().getVSACExpansionProfileListBox().getItemText(i)
										.equalsIgnoreCase(result.getCqlQualityDataSetDTO().getVsacExpIdentifier())) {
									searchDisplay.getValueSetView().getVSACExpansionProfileListBox().setSelectedIndex(i);
									break;
								}
							}
							searchDisplay.getValueSetView().getDefaultExpProfileSel().setEnabled(true);
							searchDisplay.getValueSetView().getDefaultExpProfileSel().setValue(true);
							
							expProfileToAllValueSet = result.getCqlQualityDataSetDTO().getVsacExpIdentifier();
							isExpansionProfile = true;
						} else {
							searchDisplay.getValueSetView().getDefaultExpProfileSel().setEnabled(true);
							expProfileToAllValueSet = "";
							isExpansionProfile = false;
						}
					}
					showSearchingBusy(false);
				}
			});
		}
		
	}
	
	private void addParameterEventHandlers() {
		
		/*searchDisplay.getCQLParametersView().getParameterButtonBar().getCommentButton().addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				searchDisplay.getParameterAceEditor().execCommand(AceCommand.SELECT_ALL);
				searchDisplay.getParameterAceEditor().execCommand(AceCommand.TOGGLE_BLOCK_COMMENT);
			
			}
		});*/
		
		searchDisplay.getCqlLeftNavBarPanelView().getParameterNameListBox().addDoubleClickHandler(new DoubleClickHandler() {
			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				searchDisplay.getCQLParametersView().getParameterAceEditor().clearAnnotations();
				searchDisplay.getCQLParametersView().getParameterAceEditor().removeAllMarkers();
				searchDisplay.getCQLParametersView().getParameterAceEditor().redisplay();
				System.out.println("In addParameterEventHandler on DoubleClick isPageDirty = " + searchDisplay.getCqlLeftNavBarPanelView().getIsPageDirty()
						+ " selectedIndex = " + searchDisplay.getCqlLeftNavBarPanelView().getParameterNameListBox().getSelectedIndex());
				searchDisplay.getCqlLeftNavBarPanelView().setIsDoubleClick(true);
				searchDisplay.getCqlLeftNavBarPanelView().setIsNavBarClick(false);
				if (searchDisplay.getCqlLeftNavBarPanelView().getIsPageDirty()) {
					searchDisplay.getCqlLeftNavBarPanelView().showUnsavedChangesWarning();
				} else {
					int selectedIndex = searchDisplay.getCqlLeftNavBarPanelView().getParameterNameListBox().getSelectedIndex();
					if (selectedIndex != -1) {
						final String selectedParamID = searchDisplay.getCqlLeftNavBarPanelView().getParameterNameListBox().getValue(selectedIndex);
						searchDisplay.getCqlLeftNavBarPanelView().setCurrentSelectedParamerterObjId(selectedParamID);
						if (searchDisplay.getCqlLeftNavBarPanelView().getParameterMap().get(selectedParamID) != null) {
							searchDisplay.getCQLParametersView().getParameterNameTxtArea()
									.setText(searchDisplay.getCqlLeftNavBarPanelView().getParameterMap().get(selectedParamID).getParameterName());
							searchDisplay.getCQLParametersView().getParameterAceEditor().setText(searchDisplay.getCqlLeftNavBarPanelView()
									.getParameterMap().get(selectedParamID).getParameterLogic());
							System.out.println("In Parameter DoubleClickHandler, doing setText()");
							// disable parameterName and Logic fields for
							// Default Parameter
							boolean isReadOnly = searchDisplay.getCqlLeftNavBarPanelView().getParameterMap().get(selectedParamID).isReadOnly();
							searchDisplay.getCQLParametersView().getParameterButtonBar().getDeleteButton().setTitle("Delete");

							if (MatContext.get().getLibraryLockService().checkForEditPermission()) {
								searchDisplay.getCQLParametersView().setWidgetReadOnly(!isReadOnly);
								searchDisplay.getCQLParametersView().getParameterButtonBar().getEraseButton().setEnabled(true);
							}

							// load most recent used cql artifacts
							MatContext.get().getCQLLibraryService().getUsedCqlArtifacts(
									MatContext.get().getCurrentCQLLibraryId(),
									new AsyncCallback<GetUsedCQLArtifactsResult>() {

										@Override
										public void onFailure(Throwable caught) {
											Window.alert(
													MatContext.get().getMessageDelegate().getGenericErrorMessage());
										}

										@Override
										public void onSuccess(GetUsedCQLArtifactsResult result) {
											
											CQLParameter currentParameter = searchDisplay.getCqlLeftNavBarPanelView().getParameterMap().get(selectedParamID);
											
										
											// if there are cql errors, enable the parameter delete button
											if(!result.getCqlErrors().isEmpty()) {
												searchDisplay.getCQLParametersView().getParameterButtonBar().getDeleteButton().setEnabled(true);

											} 
											
											// check if the parameter is in use, if it is disable the parameter delete button
											else if(result.getUsedCQLParameters().contains(currentParameter.getParameterName())) {
												searchDisplay.getCQLParametersView().getParameterButtonBar().getDeleteButton().setEnabled(false);
												
											}											
										
										}

									});
						}
					}

					searchDisplay.resetMessageDisplay();

				}

			}
		});
		// Parameter Save Icon Functionality
		searchDisplay.getCQLParametersView().getParameterButtonBar().getSaveButton()
				.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						if (MatContext.get().getLibraryLockService().checkForEditPermission()) {
							addAndModifyParameters();
						}
					}

				});
		// Parameter Erase Icon Functionality
		searchDisplay.getCQLParametersView().getParameterButtonBar().getEraseButton()
				.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						searchDisplay.resetMessageDisplay();
						searchDisplay.getCqlLeftNavBarPanelView().setIsDoubleClick(false);
						searchDisplay.getCqlLeftNavBarPanelView().setIsNavBarClick(false);
						if (searchDisplay.getCqlLeftNavBarPanelView().getIsPageDirty()) {
							searchDisplay.getCqlLeftNavBarPanelView().showUnsavedChangesWarning();
						} else {
							clearParameter();
						}
					}
				});
		// Parameter Delete Icon Functionality
		searchDisplay.getParameterButtonBar().getDeleteButton().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				searchDisplay.getCqlLeftNavBarPanelView().getDeleteConfirmationDialogBox().show(
						MatContext.get().getMessageDelegate().getDELETE_CONFIRMATION_PARAMETER());
					
			}

		});

		// Parameter Info Icon Functionality
		searchDisplay.getCQLParametersView().getParameterButtonBar().getInfoButton()
				.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						searchDisplay.resetMessageDisplay();
						searchDisplay.getCqlLeftNavBarPanelView().buildInfoPanel((Widget) event.getSource());

					}
				});
	}
	
	private void addDefineEventHandlers() {
		searchDisplay.getCqlLeftNavBarPanelView().getDefineNameListBox().addDoubleClickHandler(new DoubleClickHandler() {
			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				searchDisplay.getCQLDefinitionsView().getDefineAceEditor().clearAnnotations();
				searchDisplay.getCQLDefinitionsView().getDefineAceEditor().removeAllMarkers();
				searchDisplay.getCQLDefinitionsView().getDefineAceEditor().redisplay();
				searchDisplay.getCqlLeftNavBarPanelView().setIsDoubleClick(true);
				if (searchDisplay.getCqlLeftNavBarPanelView().getIsPageDirty()) {
					searchDisplay.getCqlLeftNavBarPanelView().showUnsavedChangesWarning();
				} else {
					int selectedIndex = searchDisplay.getCqlLeftNavBarPanelView().getDefineNameListBox().getSelectedIndex();
					if (selectedIndex != -1) {
						final String selectedDefinitionID = searchDisplay.getCqlLeftNavBarPanelView().getDefineNameListBox().getValue(selectedIndex);
						searchDisplay.getCqlLeftNavBarPanelView().setCurrentSelectedDefinitionObjId(selectedDefinitionID);
						if (searchDisplay.getCqlLeftNavBarPanelView().getDefinitionMap().get(selectedDefinitionID) != null) {
							searchDisplay.getCQLDefinitionsView().getDefineNameTxtArea()
									.setText(searchDisplay.getCqlLeftNavBarPanelView().getDefinitionMap().get(selectedDefinitionID).getDefinitionName());
							searchDisplay.getCQLDefinitionsView().getDefineAceEditor()
									.setText(searchDisplay.getCqlLeftNavBarPanelView().getDefinitionMap().get(selectedDefinitionID).getDefinitionLogic());
							if (searchDisplay.getCqlLeftNavBarPanelView().getDefinitionMap().get(selectedDefinitionID).getContext().equalsIgnoreCase("patient")) {
								searchDisplay.getCQLDefinitionsView().getContextDefinePATRadioBtn().setValue(true);
								searchDisplay.getCQLDefinitionsView().getContextDefinePOPRadioBtn().setValue(false);
							} else {
								searchDisplay.getCQLDefinitionsView().getContextDefinePOPRadioBtn().setValue(true);
								searchDisplay.getCQLDefinitionsView().getContextDefinePATRadioBtn().setValue(false);
							}
							// disable definitionName and fields for
							// Supplemental data definitions
							boolean isReadOnly = searchDisplay.getCqlLeftNavBarPanelView().getDefinitionMap().get(selectedDefinitionID).isSupplDataElement();
							searchDisplay.getCQLDefinitionsView().getDefineButtonBar().getDeleteButton().setTitle("Delete");

							if (MatContext.get().getLibraryLockService().checkForEditPermission()) {
								searchDisplay.getCQLDefinitionsView().setWidgetReadOnly(!isReadOnly);
								searchDisplay.getCQLDefinitionsView().getDefineButtonBar().getEraseButton().setEnabled(true);
							}

							// load most recent used cql artifacts
							MatContext.get().getCQLLibraryService().getUsedCqlArtifacts(
									MatContext.get().getCurrentCQLLibraryId(),
									new AsyncCallback<GetUsedCQLArtifactsResult>() {

										@Override
										public void onFailure(Throwable caught) {
											Window.alert(
													MatContext.get().getMessageDelegate().getGenericErrorMessage());
										}

										@Override
										public void onSuccess(GetUsedCQLArtifactsResult result) {
											
											CQLDefinition currentDefinition = searchDisplay.getCqlLeftNavBarPanelView().getDefinitionMap().get(selectedDefinitionID);

											// if there are cql errors, enable the definition delete button
											if(!result.getCqlErrors().isEmpty()) {
												searchDisplay.getCQLDefinitionsView().getDefineButtonBar().getDeleteButton().setEnabled(true);
											}
											
											// if the definition is in use, disable the definition delete button
											else if (result.getUsedCQLDefinitions().contains(currentDefinition.getDefinitionName())) {
												searchDisplay.getCQLDefinitionsView().getDefineButtonBar().getDeleteButton().setEnabled(false);
											}
											
											
										}

									});
						}
					}

					searchDisplay.resetMessageDisplay();
				}
			}
		});
		searchDisplay.getDefineButtonBar().getInsertButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				buildInsertPopUp();
			}
		});
		// Definition Save Icon Functionality
		searchDisplay.getCQLDefinitionsView().getDefineButtonBar().getSaveButton().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (MatContext.get().getLibraryLockService().checkForEditPermission()) {
					addAndModifyDefintions();
				}
			}
		});
		// Definition Erase Icon Functionality
		searchDisplay.getCQLDefinitionsView().getDefineButtonBar().getEraseButton().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				searchDisplay.resetMessageDisplay();
				searchDisplay.getCqlLeftNavBarPanelView().setIsDoubleClick(false);
				searchDisplay.getCqlLeftNavBarPanelView().setIsNavBarClick(false);
				if (searchDisplay.getCqlLeftNavBarPanelView().getIsPageDirty()) {
					searchDisplay.getCqlLeftNavBarPanelView().showUnsavedChangesWarning();
				} else {
					clearDefinition();
				}
			}
		});
		// Definition Info Icon Functionality
		searchDisplay.getCQLDefinitionsView().getDefineButtonBar().getInfoButton().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				searchDisplay.resetMessageDisplay();
				searchDisplay.getCqlLeftNavBarPanelView().buildInfoPanel((Widget) event.getSource());

			}
		});
		// Definition Delete Icon Functionality
		searchDisplay.getDefineButtonBar().getDeleteButton().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				searchDisplay.getCqlLeftNavBarPanelView().getDeleteConfirmationDialogBox().show(
						MatContext.get().getMessageDelegate().getDELETE_CONFIRMATION_DEFINITION());
						
			}
		});
	}
	
	
	private void addFunctionEventHandlers(){

		searchDisplay.getCqlLeftNavBarPanelView().getFuncNameListBox().addDoubleClickHandler(new DoubleClickHandler() {
			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				searchDisplay.getCQLFunctionsView().getFunctionBodyAceEditor().clearAnnotations();
				searchDisplay.getCQLFunctionsView().getFunctionBodyAceEditor().removeAllMarkers();
				searchDisplay.getCQLFunctionsView().getFunctionBodyAceEditor().redisplay();
				searchDisplay.getCqlLeftNavBarPanelView().setIsDoubleClick(true);
				searchDisplay.getCqlLeftNavBarPanelView().setIsNavBarClick(false);
				if (searchDisplay.getCqlLeftNavBarPanelView().getIsPageDirty()) {
					searchDisplay.getCqlLeftNavBarPanelView().showUnsavedChangesWarning();
				} else {
					int selectedIndex = searchDisplay.getCqlLeftNavBarPanelView().getFuncNameListBox().getSelectedIndex();
					if (selectedIndex != -1) {
						final String selectedFunctionId = searchDisplay.getCqlLeftNavBarPanelView().getFuncNameListBox().getValue(selectedIndex);
						searchDisplay.getCqlLeftNavBarPanelView().setCurrentSelectedFunctionObjId(selectedFunctionId);
						if (searchDisplay.getCqlLeftNavBarPanelView().getFunctionMap().get(selectedFunctionId) != null) {
							searchDisplay.getCQLFunctionsView().getFuncNameTxtArea().setText(searchDisplay.getCqlLeftNavBarPanelView().getFunctionMap().get(selectedFunctionId).getFunctionName());
							searchDisplay.getCQLFunctionsView().getFunctionBodyAceEditor().setText(searchDisplay.getCqlLeftNavBarPanelView().getFunctionMap().get(selectedFunctionId).getFunctionLogic());
							if (searchDisplay.getCqlLeftNavBarPanelView().getFunctionMap().get(selectedFunctionId).getContext().equalsIgnoreCase("patient")) {
								searchDisplay.getCQLFunctionsView().getContextFuncPATRadioBtn().setValue(true);
								searchDisplay.getCQLFunctionsView().getContextFuncPOPRadioBtn().setValue(false);
							} else {
								searchDisplay.getCQLFunctionsView().getContextFuncPOPRadioBtn().setValue(true);
								searchDisplay.getCQLFunctionsView().getContextFuncPATRadioBtn().setValue(false);
							}

							if (MatContext.get().getLibraryLockService().checkForEditPermission()) {
								searchDisplay.getCQLFunctionsView().setWidgetReadOnly(true);
								searchDisplay.getCQLFunctionsView().getFunctionButtonBar().getDeleteButton().setEnabled(true);
							}

							// load most recent used cql artifacts
							MatContext.get().getCQLLibraryService().getUsedCqlArtifacts(
									MatContext.get().getCurrentCQLLibraryId(),
									new AsyncCallback<GetUsedCQLArtifactsResult>() {

										@Override
										public void onFailure(Throwable caught) {
											Window.alert(
													MatContext.get().getMessageDelegate().getGenericErrorMessage());
										}

										@Override
										public void onSuccess(GetUsedCQLArtifactsResult result) {
											
											// if the cql file has errors, enable the function delete button
											if(!result.getCqlErrors().isEmpty()) {
												searchDisplay.getCQLFunctionsView().getFunctionButtonBar().getDeleteButton().setEnabled(true);
											}
											
											else {
												// if the function is in use, disable the function delete button
												if (result.getUsedCQLFunctions().contains(
														searchDisplay.getCqlLeftNavBarPanelView().getFunctionMap().get(selectedFunctionId).getFunctionName())) {
													searchDisplay.getCQLFunctionsView().getFunctionButtonBar().getDeleteButton().setEnabled(false);

												}
											}
											
										}

									});
						}
					}
					if (searchDisplay.getCqlLeftNavBarPanelView().getCurrentSelectedFunctionObjId() != null) {
						CQLFunctions selectedFunction = searchDisplay.getCqlLeftNavBarPanelView().getFunctionMap()
								.get(searchDisplay.getCqlLeftNavBarPanelView().getCurrentSelectedFunctionObjId());
						if (selectedFunction.getArgumentList() != null) {
							searchDisplay.getCQLFunctionsView().getFunctionArgumentList().clear();
							searchDisplay.getCQLFunctionsView().getFunctionArgumentList().addAll(selectedFunction.getArgumentList());
						} else {
							searchDisplay.getCQLFunctionsView().getFunctionArgumentList().clear();
						}
					}
				}
				searchDisplay.getCQLFunctionsView().createAddArgumentViewForFunctions(searchDisplay.getCQLFunctionsView().getFunctionArgumentList(),MatContext.get().getLibraryLockService().checkForEditPermission());
				searchDisplay.resetMessageDisplay();
			}
		});
		//Function Insert Icon Functionality
		searchDisplay.getFunctionButtonBar().getInsertButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				buildInsertPopUp();
			}
		});
		
		


		//Function Save Icon Functionality
		searchDisplay.getCQLFunctionsView().getFunctionButtonBar().getSaveButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (MatContext.get().getLibraryLockService().checkForEditPermission()) {
					addAndModifyFunction();
				}

			}
		});

		

		//Functions Erase Icon Functionality
		searchDisplay.getCQLFunctionsView().getFunctionButtonBar().getEraseButton().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				searchDisplay.resetMessageDisplay();
				searchDisplay.getCqlLeftNavBarPanelView().setIsDoubleClick(false);
				searchDisplay.getCqlLeftNavBarPanelView().setIsNavBarClick(false);
				if (searchDisplay.getCqlLeftNavBarPanelView().getIsPageDirty()) {
					searchDisplay.getCqlLeftNavBarPanelView().showUnsavedChangesWarning();
				} else {
					clearFunction();
				}
			}
		});


		
		
		//Add Function Argument functionality
		searchDisplay.getCQLFunctionsView().getAddNewArgument().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				searchDisplay.hideAceEditorAutoCompletePopUp();
				CQLFunctionArgument addNewFunctionArgument = new CQLFunctionArgument();
				AddFunctionArgumentDialogBox.showArgumentDialogBox(addNewFunctionArgument, false, searchDisplay.getCQLFunctionsView(),
						MatContext.get().getLibraryLockService().checkForEditPermission());
				searchDisplay.getCqlLeftNavBarPanelView().setIsPageDirty(true);
			}
		});

		

		

		//Function Info Icon Functionality
		searchDisplay.getCQLFunctionsView().getFunctionButtonBar().getInfoButton().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				searchDisplay.resetMessageDisplay();
				searchDisplay.getCqlLeftNavBarPanelView().buildInfoPanel((Widget) event.getSource());

			}
		});
		
		
		

		

		//Function Delete Icon Functionality
		searchDisplay.getFunctionButtonBar().getDeleteButton().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				searchDisplay.getCqlLeftNavBarPanelView().getDeleteConfirmationDialogBox().show(
						MatContext.get().getMessageDelegate().getDELETE_CONFIRMATION_FUNCTION());
					
			}

		});
		
		
		searchDisplay.getCQLFunctionsView().setObserver( new Observer() {
			
			@Override
			public void onModifyClicked(CQLFunctionArgument result) {
				// TODO Auto-generated method stub
				searchDisplay.getCqlLeftNavBarPanelView().setIsPageDirty(true);
				searchDisplay.resetMessageDisplay();
				if (result.getArgumentType().equalsIgnoreCase(CQLWorkSpaceConstants.CQL_MODEL_DATA_TYPE)) {
					getAttributesForDataType(result);
				} else {
					AddFunctionArgumentDialogBox.showArgumentDialogBox(result, true, searchDisplay.getCQLFunctionsView(),MatContext.get().getLibraryLockService().checkForEditPermission());
				}
				
			}
			
			@Override
			public void onDeleteClicked(CQLFunctionArgument result, int index) {
				searchDisplay.getCqlLeftNavBarPanelView().setIsPageDirty(true);
				Iterator<CQLFunctionArgument> iterator = searchDisplay.getFunctionArgumentList().iterator();
				searchDisplay.getFunctionArgNameMap().remove(result.getArgumentName().toLowerCase());
				while (iterator.hasNext()) {
					CQLFunctionArgument cqlFunArgument = iterator.next();
					if (cqlFunArgument.getId().equals(result.getId())) {

						iterator.remove();
						searchDisplay.createAddArgumentViewForFunctions(searchDisplay.getFunctionArgumentList(),MatContext.get().getLibraryLockService().checkForEditPermission());
						break;
					}
				}
				
			}
		});
		
	}
	private void addWarningAlertHandlers() {
		
		searchDisplay.getCqlLeftNavBarPanelView().getWarningConfirmationYesButton().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				searchDisplay.getCqlLeftNavBarPanelView().setIsPageDirty(false);
				searchDisplay.getCqlLeftNavBarPanelView().getWarningConfirmationMessageAlert().clearAlert();
				if (searchDisplay.getCqlLeftNavBarPanelView().isDoubleClick()) {
					clickEventOnListboxes();
				} else if (searchDisplay.getCqlLeftNavBarPanelView().isNavBarClick()) {
					changeSectionSelection();
				} else {
					clearViewIfDirtyNotSet();
				}
				searchDisplay.getCqlLeftNavBarPanelView().setIsNavBarClick(false);
				searchDisplay.getCqlLeftNavBarPanelView().setIsDoubleClick(false);
			}
		});

		searchDisplay.getCqlLeftNavBarPanelView().getWarningConfirmationNoButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				searchDisplay.getCqlLeftNavBarPanelView().getWarningConfirmationMessageAlert().clearAlert();
				// no was selected, don't move anywhere
				if (searchDisplay.getCqlLeftNavBarPanelView().isNavBarClick()) {
					unsetActiveMenuItem(nextSection);
				}
				if (currentSection.equals(CQLWorkSpaceConstants.CQL_FUNCTION_MENU)) {
					searchDisplay.getCqlLeftNavBarPanelView().getFuncNameListBox().setSelectedIndex(-1);
				} else if (currentSection.equals(CQLWorkSpaceConstants.CQL_PARAMETER_MENU)) {
					searchDisplay.getCqlLeftNavBarPanelView().getParameterNameListBox().setSelectedIndex(-1);
				} else if (currentSection.equals(CQLWorkSpaceConstants.CQL_DEFINE_MENU)) {
					searchDisplay.getCqlLeftNavBarPanelView().getDefineNameListBox().setSelectedIndex(-1);
				}
			}
		});
		
	}

	private void addGeneralInfoEventHandlers() {
		
		searchDisplay.getCqlGeneralInformationView().getSaveButton().addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				searchDisplay.resetMessageDisplay();
				if(searchDisplay.getCqlGeneralInformationView().getLibraryNameValue().getText().isEmpty()){
					searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert().createAlert(
							MatContext.get().getMessageDelegate().getLibraryNameRequired());
				} else {
					
					if (validator.validateForAliasNameSpecialChar(searchDisplay
							.getCqlGeneralInformationView().getLibraryNameValue().getText().trim())) {
						saveCQLGeneralInformation();
					} else {
						searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert().createAlert(
								MatContext.get().getMessageDelegate().getCqlStandAloneLibraryNameError());
					}
					
				}
			}
		});
		
		
		searchDisplay.getCqlGeneralInformationView().getCancelButton().addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				searchDisplay.resetMessageDisplay();
				searchDisplay.getCqlGeneralInformationView().getLibraryNameValue()
				.setText(cqlLibraryName);
			}
		});
	}
	
	
	/**
	 * Event Handlers for Ace Editors.
	 */
	private void addEventHandlerOnAceEditors() {
		searchDisplay.getCQLDefinitionsView().getDefineAceEditor().addKeyDownHandler(new KeyDownHandler() {

			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (!searchDisplay.getCQLDefinitionsView().getDefineAceEditor().isReadOnly()) {
					searchDisplay.resetMessageDisplay();
					searchDisplay.getCqlLeftNavBarPanelView().setIsPageDirty(true);
				}
			}
		});

		searchDisplay.getCQLParametersView().getParameterAceEditor().addKeyDownHandler(new KeyDownHandler() {

			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (!searchDisplay.getCQLParametersView().getParameterAceEditor().isReadOnly()) {
					searchDisplay.resetMessageDisplay();
					searchDisplay.getCqlLeftNavBarPanelView().setIsPageDirty(true);
				}
			}
		});

		searchDisplay.getCQLFunctionsView().getFunctionBodyAceEditor().addKeyDownHandler(new KeyDownHandler() {

			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (!searchDisplay.getCQLFunctionsView().getFunctionBodyAceEditor().isReadOnly()) {
					searchDisplay.resetMessageDisplay();
					searchDisplay.getCqlLeftNavBarPanelView().setIsPageDirty(true);
				}
			}
		});
	}

	
	private void addEventHandlersOnContextRadioButtons() {
		searchDisplay.getCQLDefinitionsView().getContextDefinePATRadioBtn().addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				searchDisplay.getCqlLeftNavBarPanelView().setIsPageDirty(true);
				if (searchDisplay.getCQLDefinitionsView().getContextDefinePATRadioBtn().getValue()) {
					searchDisplay.getCQLDefinitionsView().getContextDefinePOPRadioBtn().setValue(false);
				} else {
					searchDisplay.getCQLDefinitionsView().getContextDefinePOPRadioBtn().setValue(true);
				}

			}
		});

		searchDisplay.getCQLDefinitionsView().getContextDefinePOPRadioBtn().addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				searchDisplay.getCqlLeftNavBarPanelView().setIsPageDirty(true);
				if (searchDisplay.getCQLDefinitionsView().getContextDefinePOPRadioBtn().getValue()) {
					searchDisplay.getCQLDefinitionsView().getContextDefinePATRadioBtn().setValue(false);
				} else {
					searchDisplay.getCQLDefinitionsView().getContextDefinePATRadioBtn().setValue(true);
				}

			}
		});

		searchDisplay.getCQLFunctionsView().getContextFuncPATRadioBtn().addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				searchDisplay.getCqlLeftNavBarPanelView().setIsPageDirty(true);
				if (searchDisplay.getCQLFunctionsView().getContextFuncPATRadioBtn().getValue()) {
					searchDisplay.getCQLFunctionsView().getContextFuncPOPRadioBtn().setValue(false);
				} else {
					searchDisplay.getCQLFunctionsView().getContextFuncPOPRadioBtn().setValue(true);
				}
			}
		});

		searchDisplay.getCQLFunctionsView().getContextFuncPOPRadioBtn().addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				searchDisplay.getCqlLeftNavBarPanelView().setIsPageDirty(true);
				if (searchDisplay.getCQLFunctionsView().getContextFuncPOPRadioBtn().getValue()) {
					searchDisplay.getCQLFunctionsView().getContextFuncPATRadioBtn().setValue(false);
				} else {
					searchDisplay.getCQLFunctionsView().getContextFuncPATRadioBtn().setValue(true);
				}
			}
		});
	}
	
	private void addIncludeCQLLibraryHandlers() {
		searchDisplay.getCqlLeftNavBarPanelView().getIncludesNameListbox()
				.addDoubleClickHandler(new DoubleClickHandler() {
					@Override
					public void onDoubleClick(DoubleClickEvent event) {

						searchDisplay.getCqlLeftNavBarPanelView().setIsDoubleClick(true);
						searchDisplay.getCqlLeftNavBarPanelView().setIsNavBarClick(false);

						if (searchDisplay.getCqlLeftNavBarPanelView().getIsPageDirty()) {
							searchDisplay.getCqlLeftNavBarPanelView().showUnsavedChangesWarning();
						} else {
							int selectedIndex = searchDisplay.getCqlLeftNavBarPanelView().getIncludesNameListbox()
									.getSelectedIndex();
							if (selectedIndex != -1) {
								final String selectedIncludeLibraryID = searchDisplay.getCqlLeftNavBarPanelView()
										.getIncludesNameListbox().getValue(selectedIndex);
								searchDisplay.getCqlLeftNavBarPanelView()
										.setCurrentSelectedIncLibraryObjId(selectedIncludeLibraryID);
								if (searchDisplay.getCqlLeftNavBarPanelView().getIncludeLibraryMap()
										.get(selectedIncludeLibraryID) != null) {

									MatContext.get().getCQLLibraryService().findCQLLibraryByID(
											searchDisplay.getCqlLeftNavBarPanelView().getIncludeLibraryMap()
													.get(selectedIncludeLibraryID).getCqlLibraryId(),
											new AsyncCallback<CQLLibraryDataSetObject>() {

												@Override
												public void onSuccess(CQLLibraryDataSetObject result) {
													if (result != null) {
														searchDisplay.getIncludeView().buildIncludesReadOnlyView();

														searchDisplay.getIncludeView().getAliasNameTxtArea()
																.setText(searchDisplay.getCqlLeftNavBarPanelView()
																		.getIncludeLibraryMap()
																		.get(selectedIncludeLibraryID).getAliasName());
														searchDisplay.getIncludeView().getViewCQLEditor()
																.setText(result.getCqlText());
														searchDisplay.getIncludeView().getOwnerNameTextBox()
																.setText(searchDisplay.getCqlLeftNavBarPanelView()
																		.getOwnerName(result));
														searchDisplay.getIncludeView().getCqlLibraryNameTextBox()
																.setText(result.getCqlName());

														if (MatContext.get().getLibraryLockService()
																.checkForEditPermission()) {
															searchDisplay.getIncludeView().setWidgetReadOnly(false);
															searchDisplay.getIncludeView().getDeleteButton()
																	.setEnabled(true);
														}

														// load most recent used
														// cql artifacts
														MatContext.get().getCQLLibraryService().getUsedCqlArtifacts(
																MatContext.get().getCurrentCQLLibraryId(),
																new AsyncCallback<GetUsedCQLArtifactsResult>() {

																	@Override
																	public void onFailure(Throwable caught) {
																		Window.alert(
																				MatContext.get().getMessageDelegate()
																						.getGenericErrorMessage());
																	}

																	@Override
																	public void onSuccess(
																			GetUsedCQLArtifactsResult result) {
																		
																		
																		CQLIncludeLibrary cqlIncludeLibrary = searchDisplay
																				.getCqlLeftNavBarPanelView()
																				.getIncludeLibraryMap()
																				.get(selectedIncludeLibraryID);
																		
																		
																		// if the cql file has errors, enable the includes delete button
																		if(!result.getCqlErrors().isEmpty()) {
																			searchDisplay.getIncludeView().getDeleteButton().setEnabled(true);

																		}
																		
																		else {
																			
																			// if the includes is in use, disable the includes delete button
																			if (result.getUsedCQLLibraries().contains(
																					cqlIncludeLibrary.getCqlLibraryName() + "-" + cqlIncludeLibrary.getVersion() + "|" + cqlIncludeLibrary.getAliasName())) {
																				searchDisplay.getIncludeView().getDeleteButton().setEnabled(false);
																			}
																		}
																		
																	}

																});
													}
												}

												@Override
												public void onFailure(Throwable caught) {
													searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert()
															.createAlert(MatContext.get().getMessageDelegate()
																	.getGenericErrorMessage());
												}
											});

									searchDisplay.getIncludeView().setSelectedObject(
											searchDisplay.getCqlLeftNavBarPanelView().getIncludeLibraryMap()
													.get(selectedIncludeLibraryID).getCqlLibraryId());
									searchDisplay.getIncludeView()
											.setIncludedList(searchDisplay.getCqlLeftNavBarPanelView().getIncludedList(
													searchDisplay.getCqlLeftNavBarPanelView().getIncludeLibraryMap()));
									searchDisplay.getIncludeView().getSelectedObjectList().clear();
								}
							}
							searchDisplay.resetMessageDisplay();

						}

					}
				});

		// Includes search Button Functionality
		searchDisplay.getIncludeView().getSearchButton().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				getAllIncludeLibraryList(searchDisplay.getIncludeView().getSearchTextBox().getText().trim());
			}
		});

		// Includes search Button Focus Functionality
		searchDisplay.getIncludeView().getFocusPanel().addKeyDownHandler(new KeyDownHandler() {

			@Override
			public void onKeyDown(KeyDownEvent event) {
				// Search when enter is pressed.
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					searchDisplay.getIncludeView().getSearchButton().click();
				}
			}
		});

		// Includes Save Functionality
		searchDisplay.getIncludeView().getIncludesButtonBar().getSaveButton().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (MatContext.get().getLibraryLockService().checkForEditPermission()) {
					addIncludeLibraryInCQLLookUp();
				}
			}
		});

		// Includes Delete Functionality
		searchDisplay.getIncludeView().getDeleteButton().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				searchDisplay.getCqlLeftNavBarPanelView().getDeleteConfirmationDialogBox().show(
						MatContext.get().getMessageDelegate().getDELETE_CONFIRMATION_INCLUDE());
			}

		});

		// Includes close Functionality
		searchDisplay.getIncludeView().getCloseButton().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				// Below lines are to clear search suggestion textbox and
				// listbox
				// selection after erase.
				searchDisplay.getCqlLeftNavBarPanelView().getSearchSuggestIncludeTextBox().setText("");
				if (searchDisplay.getCqlLeftNavBarPanelView().getIncludesNameListbox().getSelectedIndex() >= 0) {
					searchDisplay.getCqlLeftNavBarPanelView().getIncludesNameListbox().setItemSelected(
							searchDisplay.getCqlLeftNavBarPanelView().getIncludesNameListbox().getSelectedIndex(),
							false);
				}

				searchDisplay.buildIncludesView();
				SaveCQLLibraryResult cqlLibrarySearchModel = new SaveCQLLibraryResult();
				cqlLibrarySearchModel
						.setCqlLibraryDataSetObjects(searchDisplay.getCqlLeftNavBarPanelView().getIncludeLibraryList());
				searchDisplay.getIncludeView().setIncludedList(searchDisplay.getCqlLeftNavBarPanelView()
						.getIncludedList(searchDisplay.getCqlLeftNavBarPanelView().getIncludeLibraryMap()));
				searchDisplay.getIncludeView().buildIncludeLibraryCellTable(cqlLibrarySearchModel,
						MatContext.get().getLibraryLockService().checkForEditPermission());
				searchDisplay.getIncludeView()
						.setWidgetReadOnly(MatContext.get().getLibraryLockService().checkForEditPermission());
				if (searchDisplay.getCqlLeftNavBarPanelView().getIncludesNameListbox()
						.getItemCount() >= CQLWorkSpaceConstants.VALID_INCLUDE_COUNT) {
					searchDisplay.getCqlLeftNavBarPanelView().getWarningMessageAlert()
							.createAlert(MatContext.get().getMessageDelegate().getCqlLimitWarningMessage());
				} else {
					searchDisplay.getCqlLeftNavBarPanelView().getWarningMessageAlert().clearAlert();
				}
			}
		});

			// Includes Erase Functionality
		searchDisplay.getIncludeView().getEraseButton().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				searchDisplay.resetMessageDisplay();
				searchDisplay.getCqlLeftNavBarPanelView().setIsDoubleClick(false);
				searchDisplay.getCqlLeftNavBarPanelView().setIsNavBarClick(false);
				clearAlias();
				if (searchDisplay.getCqlLeftNavBarPanelView().getIncludesNameListbox()
						.getItemCount() >= CQLWorkSpaceConstants.VALID_INCLUDE_COUNT) {
					searchDisplay.getCqlLeftNavBarPanelView().getWarningMessageAlert()
							.createAlert(MatContext.get().getMessageDelegate().getCqlLimitWarningMessage());
				} else {
					searchDisplay.getCqlLeftNavBarPanelView().getWarningMessageAlert().clearAlert();
				}
			}
		});

		// Includes Celltable Observer for CheckBox
		searchDisplay.getIncludeView().setObserver(new CQLIncludeLibraryView.Observer() {

			@Override
			public void onCheckBoxClicked(CQLLibraryDataSetObject result) {
				MatContext.get().getCQLLibraryService().findCQLLibraryByID(result.getId(),
						new AsyncCallback<CQLLibraryDataSetObject>() {

							@Override
							public void onFailure(Throwable caught) {
								Window.alert(MatContext.get().getMessageDelegate().getGenericErrorMessage());
								Window.alert(MatContext.get().getMessageDelegate().getGenericErrorMessage());
							}

							@Override
							public void onSuccess(CQLLibraryDataSetObject result) {
								searchDisplay.getIncludeView().getViewCQLEditor().setText(result.getCqlText());
							}
						});
			}
		});
	}
	
	
	/**
	 * Adds and modify function.
	 */
	protected void addAndModifyFunction() {
		searchDisplay.resetMessageDisplay();
		final String functionName = searchDisplay.getCQLFunctionsView().getFuncNameTxtArea().getText();
		String functionBody = searchDisplay.getCQLFunctionsView().getFunctionBodyAceEditor().getText();
		String funcContext = "";
		if (searchDisplay.getCQLFunctionsView().getContextFuncPATRadioBtn().getValue()) {
			funcContext = "Patient";
		} else {
			funcContext = "Population";
		}
		if (!functionName.isEmpty()) {
			if (!validator.validateForSpecialChar(functionName.trim())) {

				CQLFunctions function = new CQLFunctions();
				function.setFunctionLogic(functionBody);
				function.setFunctionName(functionName);
				function.setArgumentList(searchDisplay.getCQLFunctionsView().getFunctionArgumentList());
				function.setContext(funcContext);
				CQLFunctions toBeModifiedParamObj = null;
				
				if (searchDisplay.getCqlLeftNavBarPanelView().getCurrentSelectedFunctionObjId() != null) {
					toBeModifiedParamObj = searchDisplay.getCqlLeftNavBarPanelView().getFunctionMap()
							.get(searchDisplay.getCqlLeftNavBarPanelView().getCurrentSelectedFunctionObjId());
				}
				showSearchingBusy(true);
					MatContext.get().getCQLLibraryService().saveAndModifyFunctions(MatContext.get().getCurrentCQLLibraryId(),
							toBeModifiedParamObj, function, searchDisplay.getCqlLeftNavBarPanelView().getViewFunctions(),
							new AsyncCallback<SaveUpdateCQLResult>() {

								@Override
								public void onFailure(Throwable caught) {
									searchDisplay.getCqlLeftNavBarPanelView().setCurrentSelectedFunctionObjId(null);
									searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert().createAlert(
											MatContext.get().getMessageDelegate().getGenericErrorMessage());
									showSearchingBusy(false);
								}

								@Override
								public void onSuccess(SaveUpdateCQLResult result) {
									if(result != null ){
										if (result.isSuccess()) {

											searchDisplay.getCqlLeftNavBarPanelView().setViewFunctions(result.getCqlModel().getCqlFunctions());
											MatContext.get()
													.setFuncs(getFunctionList(result.getCqlModel().getCqlFunctions()));
											searchDisplay.getCqlLeftNavBarPanelView().setCurrentSelectedFunctionObjId(result.getFunction().getId());
											searchDisplay.getCqlLeftNavBarPanelView().clearAndAddFunctionsNamesToListBox();
											searchDisplay.getCqlLeftNavBarPanelView().updateFunctionMap();
											searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert().clearAlert();
											searchDisplay.getCqlLeftNavBarPanelView().getSuccessMessageAlert().setVisible(true);

											searchDisplay.getCQLFunctionsView().getFuncNameTxtArea()
													.setText(result.getFunction().getFunctionName());
											searchDisplay.getCQLFunctionsView().getFunctionBodyAceEditor()
													.setText(result.getFunction().getFunctionLogic());
											searchDisplay.getCqlLeftNavBarPanelView().setIsPageDirty(false);
											searchDisplay.getCQLFunctionsView().getFunctionBodyAceEditor().clearAnnotations();
											searchDisplay.getCQLFunctionsView().getFunctionBodyAceEditor().removeAllMarkers();
											searchDisplay.getCQLFunctionsView().getFunctionBodyAceEditor().redisplay();

											if (validateCQLArtifact(result, currentSection)) {
												searchDisplay.getCqlLeftNavBarPanelView().getSuccessMessageAlert().clearAlert();
												searchDisplay.getCqlLeftNavBarPanelView().getWarningMessageAlert().createAlert(MatContext.get()
														.getMessageDelegate().getSUCESS_FUNCTION_MODIFY_WITH_ERRORS());

											} else {
												searchDisplay.getCqlLeftNavBarPanelView().getSuccessMessageAlert().createAlert(
														MatContext.get().getMessageDelegate().getSUCESS_FUNCTION_MODIFY());
											}
											
											
											if (validateCQLArtifact(result, currentSection)) {
												searchDisplay.getCqlLeftNavBarPanelView().getSuccessMessageAlert().clearAlert();
												searchDisplay.getCqlLeftNavBarPanelView().getWarningMessageAlert().createAlert(MatContext.get()
														.getMessageDelegate().getSUCESS_FUNCTION_MODIFY_WITH_ERRORS());

											} else {
												searchDisplay.getCqlLeftNavBarPanelView().getSuccessMessageAlert().createAlert(
														MatContext.get().getMessageDelegate().getSUCESS_FUNCTION_MODIFY());
											}
											
											// if there are errors, enable the function delete button
											if(!result.getCqlErrors().isEmpty()) {
												searchDisplay.getCQLFunctionsView().getFunctionButtonBar().getDeleteButton().setEnabled(true);
											}
											
											else {
												// if the saved function is in use, then disable the delete button
												if (result.getUsedCQLArtifacts().getUsedCQLFunctions().contains(functionName)) {
													searchDisplay.getCQLFunctionsView().getFunctionButtonBar().getDeleteButton().setEnabled(false);
												}
												
												else {
													searchDisplay.getCQLFunctionsView().getFunctionButtonBar().getDeleteButton().setEnabled(true);
												}
											}
											
											searchDisplay.getCQLFunctionsView().getFunctionBodyAceEditor().setAnnotations();
											searchDisplay.getCQLFunctionsView().getFunctionBodyAceEditor().redisplay();

										} else if (result.getFailureReason() == 1) {
											searchDisplay.getCqlLeftNavBarPanelView().getSuccessMessageAlert().clearAlert();
											searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert().createAlert(MatContext.get()
													.getMessageDelegate().getERROR_DUPLICATE_IDENTIFIER_NAME());
											searchDisplay.getCQLFunctionsView().getFuncNameTxtArea().setText(functionName.trim());
										} else if (result.getFailureReason() == 2) {
											searchDisplay.getCqlLeftNavBarPanelView().getSuccessMessageAlert().clearAlert();
											searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert()
													.createAlert("Unable to find Node to modify.");
											searchDisplay.getCQLFunctionsView().getFuncNameTxtArea().setText(functionName.trim());
										} else if (result.getFailureReason() == 3) {
											searchDisplay.getCqlLeftNavBarPanelView().getSuccessMessageAlert().clearAlert();
											searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert().createAlert(MatContext.get()
													.getMessageDelegate().getERROR_FUNCTION_NAME_NO_SPECIAL_CHAR());
											searchDisplay.getCQLFunctionsView().getFuncNameTxtArea().setText(functionName.trim());
											if (result.getFunction() != null) {
												searchDisplay.createAddArgumentViewForFunctions(
														result.getFunction().getArgumentList());
											}
										}

									}
									showSearchingBusy(false);			
								}
							});
				
			} else {
				searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert()
						.createAlert(MatContext.get().getMessageDelegate().getERROR_FUNCTION_NAME_NO_SPECIAL_CHAR());
				searchDisplay.getCQLFunctionsView().getFuncNameTxtArea().setText(functionName.trim());
			}

		} else {
			searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert()
					.createAlert(MatContext.get().getMessageDelegate().getERROR_SAVE_CQL_FUNCTION());
			searchDisplay.getCQLFunctionsView().getFuncNameTxtArea().setText(functionName.trim());
		}
		
	}

	/**
	 * Adds the and modify parameters.
	 */
	private void addAndModifyParameters() {
		
		searchDisplay.resetMessageDisplay();
		final String parameterName = searchDisplay.getCQLParametersView().getParameterNameTxtArea().getText();
		String parameterLogic = searchDisplay.getCQLParametersView().getParameterAceEditor().getText();
		if (!parameterName.isEmpty()) {

			if (!validator.validateForSpecialChar(parameterName.trim())) {

				CQLParameter parameter = new CQLParameter();
				parameter.setParameterLogic(parameterLogic);
				parameter.setParameterName(parameterName);
				CQLParameter toBeModifiedParamObj = null;
				
				if (searchDisplay.getCqlLeftNavBarPanelView().getCurrentSelectedParamerterObjId() != null) {
					toBeModifiedParamObj = searchDisplay.getCqlLeftNavBarPanelView().getParameterMap()
							.get(searchDisplay.getCqlLeftNavBarPanelView().getCurrentSelectedParamerterObjId());		
				} 
				showSearchingBusy(true);
				MatContext.get().getCQLLibraryService().saveAndModifyParameters(MatContext.get().getCurrentCQLLibraryId(),
							toBeModifiedParamObj, parameter, searchDisplay.getCqlLeftNavBarPanelView().getViewParameterList(),
							new AsyncCallback<SaveUpdateCQLResult>() {

								@Override
								public void onFailure(Throwable caught) {
									searchDisplay.getCqlLeftNavBarPanelView().setCurrentSelectedParamerterObjId(null);
									searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert().createAlert(
											MatContext.get().getMessageDelegate().getGenericErrorMessage());
									showSearchingBusy(false);
								}

								@Override
								public void onSuccess(SaveUpdateCQLResult result) {
									if(result != null){
										if (result.isSuccess()) {
											searchDisplay.getCqlLeftNavBarPanelView().setViewParameterList(result.getCqlModel().getCqlParameters());
											MatContext.get().setParameters(
													getParamaterList(result.getCqlModel().getCqlParameters()));
											searchDisplay.getCqlLeftNavBarPanelView().setCurrentSelectedParamerterObjId(result.getParameter().getId());
											searchDisplay.getCqlLeftNavBarPanelView().clearAndAddParameterNamesToListBox();
											searchDisplay.getCqlLeftNavBarPanelView().updateParamMap();
											searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert().clearAlert();
											searchDisplay.getCQLParametersView().getParameterNameTxtArea()
													.setText(result.getParameter().getParameterName());
											searchDisplay.getCQLParametersView().getParameterAceEditor()
													.setText(result.getParameter().getParameterLogic());
											searchDisplay.getCqlLeftNavBarPanelView().setIsPageDirty(false);
											searchDisplay.getCQLParametersView().getParameterAceEditor().clearAnnotations();
											searchDisplay.getCQLParametersView().getParameterAceEditor().removeAllMarkers();
											searchDisplay.getCQLParametersView().getParameterAceEditor().redisplay();

											if (validateCQLArtifact(result, currentSection)) {
												searchDisplay.getCqlLeftNavBarPanelView().getWarningMessageAlert().createAlert(MatContext.get()
														.getMessageDelegate().getSUCESS_PARAMETER_MODIFY_WITH_ERRORS());

											} else {
												searchDisplay.getCqlLeftNavBarPanelView().getSuccessMessageAlert().createAlert(
														MatContext.get().getMessageDelegate().getSUCESS_PARAMETER_MODIFY());
											}
											
											
											// if there are errors, enable the parameter delete button
											if(!result.getCqlErrors().isEmpty()) {
												searchDisplay.getCQLParametersView().getParameterButtonBar().getDeleteButton().setEnabled(true);
											}
											
											else {
												// if the saved parameter is in use, then disable the delete button
												if (result.getUsedCQLArtifacts().getUsedCQLParameters().contains(parameterName)) {
													searchDisplay.getCQLParametersView().getParameterButtonBar().getDeleteButton().setEnabled(false);
												}
												
												else {
													searchDisplay.getCQLParametersView().getParameterButtonBar().getDeleteButton().setEnabled(true);
												}
											}
											
											searchDisplay.getCQLParametersView().getParameterAceEditor().setAnnotations();
											searchDisplay.getCQLParametersView().getParameterAceEditor().redisplay();

										} else if (result.getFailureReason() == 1) {
											searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert().createAlert(MatContext.get()
													.getMessageDelegate().getERROR_DUPLICATE_IDENTIFIER_NAME());
											searchDisplay.getCQLParametersView().getParameterNameTxtArea().setText(parameterName.trim());
										} else if (result.getFailureReason() == 2) {
											searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert()
													.createAlert("Unable to find Node to modify.");
											searchDisplay.getCQLParametersView().getParameterNameTxtArea().setText(parameterName.trim());
										} else if (result.getFailureReason() == 3) {
											searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert().createAlert(MatContext.get()
													.getMessageDelegate().getERROR_PARAMETER_NAME_NO_SPECIAL_CHAR());
											searchDisplay.getCQLParametersView().getParameterNameTxtArea().setText(parameterName.trim());
										}
									}
									showSearchingBusy(false);
									
								}
							});
				

			} else {
				searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert()
						.createAlert(MatContext.get().getMessageDelegate().getERROR_PARAMETER_NAME_NO_SPECIAL_CHAR());
				searchDisplay.getCQLParametersView().getParameterNameTxtArea().setText(parameterName.trim());
			}

		} else {
			searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert()
					.createAlert(MatContext.get().getMessageDelegate().getERROR_SAVE_CQL_PARAMETER());
			searchDisplay.getCQLParametersView().getParameterNameTxtArea().setText(parameterName.trim());
		}
		

	}
	
	/**
	 * This method is called to Add/Modify Definitions into Library Xml.
	 * 
	 */
	private void addAndModifyDefintions() {
		
		searchDisplay.resetMessageDisplay();
		final String definitionName = searchDisplay.getCQLDefinitionsView().getDefineNameTxtArea().getText();
		String definitionLogic = searchDisplay.getCQLDefinitionsView().getDefineAceEditor().getText();
		String defineContext = "";
		if (searchDisplay.getCQLDefinitionsView().getContextDefinePATRadioBtn().getValue()) {
			defineContext = "Patient";
		} else {
			defineContext = "Population";
		}
		if (!definitionName.isEmpty()) {

			if (!validator.validateForSpecialChar(definitionName.trim())) {

				final CQLDefinition define = new CQLDefinition();
				define.setDefinitionName(definitionName);
				define.setDefinitionLogic(definitionLogic);
				define.setContext(defineContext);
				CQLDefinition toBeModifiedObj = null;
				
				if (searchDisplay.getCqlLeftNavBarPanelView().getCurrentSelectedDefinitionObjId() != null) {
					toBeModifiedObj = searchDisplay.getCqlLeftNavBarPanelView().getDefinitionMap()
							.get(searchDisplay.getCqlLeftNavBarPanelView().getCurrentSelectedDefinitionObjId());
				}
				showSearchingBusy(true);
				MatContext.get().getCQLLibraryService().saveAndModifyDefinitions(
							MatContext.get().getCurrentCQLLibraryId(), toBeModifiedObj, define,
							searchDisplay.getCqlLeftNavBarPanelView().getViewDefinitions(), new AsyncCallback<SaveUpdateCQLResult>() {

								@Override
								public void onFailure(Throwable caught) {
									searchDisplay.getCqlLeftNavBarPanelView().setCurrentSelectedDefinitionObjId(null);
									searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert().createAlert(
											MatContext.get().getMessageDelegate().getGenericErrorMessage());
									showSearchingBusy(false);
								}

								@Override
								public void onSuccess(SaveUpdateCQLResult result) {
									if(result != null){
										if (result.isSuccess()) {
											searchDisplay.getCqlLeftNavBarPanelView().setViewDefinitions(result.getCqlModel().getDefinitionList());
											MatContext.get().setDefinitions(
													getDefinitionList(result.getCqlModel().getDefinitionList()));
											searchDisplay.getCqlLeftNavBarPanelView().setCurrentSelectedDefinitionObjId(result.getDefinition().getId());
											searchDisplay.getCqlLeftNavBarPanelView().clearAndAddDefinitionNamesToListBox();
											searchDisplay.getCqlLeftNavBarPanelView().updateDefineMap();
											searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert().clearAlert();
											searchDisplay.getCQLDefinitionsView().getDefineNameTxtArea()
													.setText(result.getDefinition().getDefinitionName());
											searchDisplay.getCQLDefinitionsView().getDefineAceEditor()
													.setText(result.getDefinition().getDefinitionLogic());
											searchDisplay.getCqlLeftNavBarPanelView().setIsPageDirty(false);
											searchDisplay.getCQLDefinitionsView().getDefineAceEditor().clearAnnotations();
											searchDisplay.getCQLDefinitionsView().getDefineAceEditor().removeAllMarkers();
											searchDisplay.getCQLDefinitionsView().getDefineAceEditor().redisplay();

											if (validateCQLArtifact(result, currentSection)) {
												searchDisplay.getCqlLeftNavBarPanelView().getWarningMessageAlert().createAlert(MatContext.get()
														.getMessageDelegate().getSUCESS_DEFINITION_MODIFY_WITH_ERRORS());
											} else {
												searchDisplay.getCqlLeftNavBarPanelView().getSuccessMessageAlert().createAlert(MatContext.get()
														.getMessageDelegate().getSUCESS_DEFINITION_MODIFY());
											}
											
											// if there are errors, enable the definition button
											if(!result.getCqlErrors().isEmpty()) {
												searchDisplay.getCQLDefinitionsView().getDefineButtonBar().getDeleteButton().setEnabled(true);
											}
											
											else {
												// if the saved definition is in use, then disable the delete button
												if (result.getUsedCQLArtifacts().getUsedCQLDefinitions().contains(definitionName)) {
													searchDisplay.getCQLDefinitionsView().getDefineButtonBar().getDeleteButton().setEnabled(false);
												}
												
												else {
													searchDisplay.getCQLDefinitionsView().getDefineButtonBar().getDeleteButton().setEnabled(true);
												}
											}
											
											searchDisplay.getCQLDefinitionsView().getDefineAceEditor().setAnnotations();
											searchDisplay.getCQLDefinitionsView().getDefineAceEditor().redisplay();

										} else {
											if (result.getFailureReason() == 1) {
												searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert().createAlert(MatContext.get()
														.getMessageDelegate().getERROR_DUPLICATE_IDENTIFIER_NAME());
												searchDisplay.getCQLDefinitionsView().getDefineNameTxtArea().setText(definitionName.trim());
											} else if (result.getFailureReason() == 2) {
												searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert()
														.createAlert("Unable to find Node to modify.");
												searchDisplay.getCQLDefinitionsView().getDefineNameTxtArea().setText(definitionName.trim());
											} else if (result.getFailureReason() == 3) {
												searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert().createAlert(MatContext.get()
														.getMessageDelegate().getERROR_DEFINITION_NAME_NO_SPECIAL_CHAR());
												searchDisplay.getCQLDefinitionsView().getDefineNameTxtArea().setText(definitionName.trim());
											}
										}

									}
									showSearchingBusy(false);
																	}
							});
			} else {
				searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert()
						.createAlert(MatContext.get().getMessageDelegate().getERROR_DEFINITION_NAME_NO_SPECIAL_CHAR());
				searchDisplay.getCQLDefinitionsView().getDefineNameTxtArea().setText(definitionName.trim());
			}

		} else {
			searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert()
					.createAlert(MatContext.get().getMessageDelegate().getERROR_SAVE_CQL_DEFINITION());
			searchDisplay.getCQLDefinitionsView().getDefineNameTxtArea().setText(definitionName.trim());
		}
		
	}
	
	/**
	 * Adds the include library in CQL look up.
	 */
	private void addIncludeLibraryInCQLLookUp() {
		searchDisplay.resetMessageDisplay();
		if(searchDisplay.getCqlLeftNavBarPanelView().getIncludesNameListbox().getItemCount() >= CQLWorkSpaceConstants.VALID_INCLUDE_COUNT){
			searchDisplay.getCqlLeftNavBarPanelView().getWarningMessageAlert().createAlert(MatContext.get().getMessageDelegate().getCqlLimitWarningMessage());
			return;
			
		} else{
			searchDisplay.getCqlLeftNavBarPanelView().getWarningMessageAlert().clearAlert();
		}	
		final String aliasName = searchDisplay.getIncludeView().getAliasNameTxtArea().getText();
		
		if (!aliasName.isEmpty() && searchDisplay.getIncludeView().getSelectedObjectList().size()>0) {
			//functioanlity to add Include Library
			CQLLibraryDataSetObject cqlLibraryDataSetObject = searchDisplay.getIncludeView().getSelectedObjectList().get(0);
			
			if (validator.validateForAliasNameSpecialChar(aliasName.trim())) {
				
				CQLIncludeLibrary incLibrary = new CQLIncludeLibrary();
				incLibrary.setAliasName(aliasName);
				incLibrary.setCqlLibraryId(cqlLibraryDataSetObject.getId());
				String versionValue = cqlLibraryDataSetObject.getVersion().replace("v", "")+"."+cqlLibraryDataSetObject.getRevisionNumber();
				incLibrary.setVersion(versionValue);
				incLibrary.setCqlLibraryName(cqlLibraryDataSetObject.getCqlName());
				
				if (searchDisplay.getCqlLeftNavBarPanelView().getCurrentSelectedIncLibraryObjId() == null) {
					//this is just to add include library and not modify
					MatContext.get().getCQLLibraryService().saveIncludeLibrayInCQLLookUp(MatContext.get().getCurrentCQLLibraryId(), 
							null, incLibrary, searchDisplay.getCqlLeftNavBarPanelView().getViewIncludeLibrarys(), new AsyncCallback<SaveUpdateCQLResult>() {

								@Override
								public void onFailure(Throwable caught) {
									searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert().createAlert(
											MatContext.get().getMessageDelegate().getGenericErrorMessage());
									
								}

								@Override
								public void onSuccess(SaveUpdateCQLResult result) {
									if(result != null){
										if (result.isSuccess()) {
											searchDisplay.resetMessageDisplay();
											searchDisplay.getCqlLeftNavBarPanelView().setIsPageDirty(false);
											searchDisplay.getCqlLeftNavBarPanelView().setViewIncludeLibrarys(result.getCqlModel().getCqlIncludeLibrarys());
											MatContext.get().setIncludes(getIncludesList(result.getCqlModel().getCqlIncludeLibrarys()));
											searchDisplay.getCqlLeftNavBarPanelView().clearAndAddAliasNamesToListBox();
											searchDisplay.getCqlLeftNavBarPanelView().udpateIncludeLibraryMap();
											searchDisplay.getIncludeView().setIncludedList(searchDisplay.getCqlLeftNavBarPanelView()
													.getIncludedList(searchDisplay.getCqlLeftNavBarPanelView().getIncludeLibraryMap()));
											searchDisplay.getCqlLeftNavBarPanelView().getSuccessMessageAlert().createAlert(MatContext.get()
													.getMessageDelegate().getIncludeLibrarySuccessMessage(result.getIncludeLibrary().getAliasName()));
											clearAlias();
											if(searchDisplay.getCqlLeftNavBarPanelView().getIncludesNameListbox().getItemCount() >= CQLWorkSpaceConstants.VALID_INCLUDE_COUNT){
												searchDisplay.getCqlLeftNavBarPanelView().getWarningMessageAlert().createAlert(MatContext.get().getMessageDelegate().getCqlLimitWarningMessage());
											} else{
												searchDisplay.getCqlLeftNavBarPanelView().getWarningMessageAlert().clearAlert();
											}
										
											
										}  else if (result.getFailureReason() == 1) {
											searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert().createAlert(MatContext.get()
													.getMessageDelegate().getERROR_INCLUDE_ALIAS_NAME_NO_SPECIAL_CHAR());
											searchDisplay.getAliasNameTxtArea().setText(aliasName.trim());
										} else if (result.getFailureReason() == 2) {
											searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert().createAlert("Missing includes library tag.");
											searchDisplay.getAliasNameTxtArea().setText(aliasName.trim());
										}  else if(result.getFailureReason() == 3){
											searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert().createAlert(MatContext.get()
													.getMessageDelegate().getERROR_INCLUDE_ALIAS_NAME_NO_SPECIAL_CHAR());
											searchDisplay.getAliasNameTxtArea().setText(aliasName.trim());
										}
									}
								}
							});
				}
			
			} else {
				searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert()
				.createAlert(MatContext.get().getMessageDelegate().getERROR_INCLUDE_ALIAS_NAME_NO_SPECIAL_CHAR());
				searchDisplay.getAliasNameTxtArea().setText(aliasName.trim());
			}
			
			
		} else {
			searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert().createAlert(
					MatContext.get().getMessageDelegate().getSAVE_INCLUDE_LIBRARY_VALIATION_ERROR());
		}
	}
	
	
	/**
	 * This method Clears parameter view on Erase Button click when isPageDirty
	 * is not set.
	 */
	private void clearParameter() {
		searchDisplay.getCqlLeftNavBarPanelView().setCurrentSelectedParamerterObjId(null);
		searchDisplay.getCQLParametersView().getParameterAceEditor().clearAnnotations();
		searchDisplay.getCqlLeftNavBarPanelView().setIsPageDirty(false);
		if ((searchDisplay.getCQLParametersView().getParameterAceEditor().getText() != null)) {
			searchDisplay.getCQLParametersView().getParameterAceEditor().setText("");
		}
		if ((searchDisplay.getCQLParametersView().getParameterNameTxtArea() != null)) {
			searchDisplay.getCQLParametersView().getParameterNameTxtArea().setText("");
		}

		if (MatContext.get().getLibraryLockService().checkForEditPermission()) {
			searchDisplay.getCQLParametersView().setWidgetReadOnly(MatContext.get().getLibraryLockService().checkForEditPermission());
		}
		// Below lines are to clear search suggestion textbox and listbox
		// selection after erase.
		searchDisplay.getCqlLeftNavBarPanelView().getSearchSuggestParamTextBox().setText("");
		if (searchDisplay.getCqlLeftNavBarPanelView().getParameterNameListBox().getSelectedIndex() >= 0) {
			searchDisplay.getCqlLeftNavBarPanelView().getParameterNameListBox()
					.setItemSelected(searchDisplay.getCqlLeftNavBarPanelView().getParameterNameListBox().getSelectedIndex(), false);
		}

		searchDisplay.getParameterButtonBar().getDeleteButton().setEnabled(false);
	}

	/**
	 * This method Clears Definition view on Erase Button click when isPageDirty
	 * is not set.
	 */
	private void clearDefinition() {
		searchDisplay.getCqlLeftNavBarPanelView().setCurrentSelectedDefinitionObjId(null);
		searchDisplay.getCQLDefinitionsView().getDefineAceEditor().clearAnnotations();
		searchDisplay.getCqlLeftNavBarPanelView().setIsPageDirty(false);
		if ((searchDisplay.getCQLDefinitionsView().getDefineAceEditor().getText() != null)) {
			searchDisplay.getCQLDefinitionsView().getDefineAceEditor().setText("");
		}
		if ((searchDisplay.getCQLDefinitionsView().getDefineNameTxtArea() != null)) {
			searchDisplay.getCQLDefinitionsView().getDefineNameTxtArea().setText("");
		}
		// Below lines are to clear search suggestion textbox and listbox
		// selection after erase.
		searchDisplay.getCqlLeftNavBarPanelView().getSearchSuggestDefineTextBox().setText("");
		if (searchDisplay.getCqlLeftNavBarPanelView().getDefineNameListBox().getSelectedIndex() >= 0) {
			searchDisplay.getCqlLeftNavBarPanelView().getDefineNameListBox()
					.setItemSelected(searchDisplay.getCqlLeftNavBarPanelView().getDefineNameListBox().getSelectedIndex(), false);
		}

		// Functionality to reset the disabled features for supplemental data
		// definitions when erased.
		searchDisplay.getCQLDefinitionsView().getDefineNameTxtArea().setEnabled(true);
		searchDisplay.getCQLDefinitionsView().getDefineAceEditor().setReadOnly(false);
		searchDisplay.getCQLDefinitionsView().getContextDefinePATRadioBtn().setEnabled(true);
		searchDisplay.getCQLDefinitionsView().getContextDefinePOPRadioBtn().setEnabled(true);
		searchDisplay.getDefineButtonBar().getSaveButton().setEnabled(true);
		searchDisplay.getDefineButtonBar().getDeleteButton().setEnabled(false);
		searchDisplay.getDefineButtonBar().getInsertButton().setEnabled(true);
		searchDisplay.getDefineButtonBar().getTimingExpButton().setEnabled(true);
		searchDisplay.getCQLDefinitionsView().getContextDefinePATRadioBtn().setValue(true);
		searchDisplay.getCQLDefinitionsView().getContextDefinePOPRadioBtn().setValue(false);
	}

	/**
	 * This method Clears Function view on Erase Button click when isPageDirty
	 * is not set.
	 */
	private void clearFunction() {
		searchDisplay.getCqlLeftNavBarPanelView().setCurrentSelectedFunctionObjId(null);
		searchDisplay.getCQLFunctionsView().getFunctionArgumentList().clear();
		searchDisplay.getCQLFunctionsView().getFunctionArgNameMap().clear();
		searchDisplay.getCQLFunctionsView().getFunctionBodyAceEditor().clearAnnotations();
		searchDisplay.createAddArgumentViewForFunctions(new ArrayList<CQLFunctionArgument>());
		searchDisplay.getCqlLeftNavBarPanelView().setIsPageDirty(false);
		if ((searchDisplay.getCQLFunctionsView().getFunctionBodyAceEditor().getText() != null)) {
			searchDisplay.getCQLFunctionsView().getFunctionBodyAceEditor().setText("");
		}
		if ((searchDisplay.getCQLFunctionsView().getFuncNameTxtArea() != null)) {
			searchDisplay.getCQLFunctionsView().getFuncNameTxtArea().setText("");
		}
		// Below lines are to clear search suggestion textbox and listbox
		// selection after erase.
		searchDisplay.getCqlLeftNavBarPanelView().getSearchSuggestFuncTextBox().setText("");
		if (searchDisplay.getCqlLeftNavBarPanelView().getFuncNameListBox().getSelectedIndex() >= 0) {
			searchDisplay.getCqlLeftNavBarPanelView().getFuncNameListBox().setItemSelected(
					searchDisplay.getCqlLeftNavBarPanelView().getFuncNameListBox().getSelectedIndex(),
					false);
		}
		searchDisplay.getCQLFunctionsView().getContextFuncPATRadioBtn().setValue(true);
		searchDisplay.getCQLFunctionsView().getContextFuncPOPRadioBtn().setValue(false);
		searchDisplay.getFunctionButtonBar().getDeleteButton().setEnabled(false);
	}
	
	 /* Build Insert Pop up.
	 */
	private void buildInsertPopUp() {
		searchDisplay.resetMessageDisplay();
		InsertIntoAceEditorDialogBox.showListOfItemAvailableForInsertDialogBox(searchDisplay
				.getCqlLeftNavBarPanelView(), curAceEditor);
		searchDisplay.getCqlLeftNavBarPanelView().setIsPageDirty(true);
	}
	
	protected void deleteDefinition() {
		searchDisplay.resetMessageDisplay();
		final String definitionName = searchDisplay.getDefineNameTxtArea().getText();
		String definitionLogic = searchDisplay.getDefineAceEditor().getText();
		String defineContext = "";
		if (searchDisplay.getContextDefinePATRadioBtn().getValue()) {
			defineContext = "Patient";
		} else {
			defineContext = "Population";
		}
		if (!definitionName.isEmpty()) {

			if (!validator.validateForSpecialChar(definitionName.trim())) {

				final CQLDefinition define = new CQLDefinition();
				define.setDefinitionName(definitionName);
				define.setDefinitionLogic(definitionLogic);
				define.setContext(defineContext);

				if (searchDisplay.getCqlLeftNavBarPanelView().getCurrentSelectedDefinitionObjId() != null) {
					CQLDefinition toBeModifiedObj = searchDisplay.getCqlLeftNavBarPanelView().getDefinitionMap()
							.get(searchDisplay.getCqlLeftNavBarPanelView().getCurrentSelectedDefinitionObjId());
					showSearchingBusy(true);
					MatContext.get().getCQLLibraryService().deleteDefinition(MatContext.get().getCurrentCQLLibraryId(),
							toBeModifiedObj, define, searchDisplay.getCqlLeftNavBarPanelView().getViewDefinitions(),
							new AsyncCallback<SaveUpdateCQLResult>() {

								@Override
								public void onFailure(Throwable caught) {
									searchDisplay.getCqlLeftNavBarPanelView().setCurrentSelectedDefinitionObjId(null);
									searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert().createAlert(
											MatContext.get().getMessageDelegate().getGenericErrorMessage());
									showSearchingBusy(false);
								}

								@Override
								public void onSuccess(SaveUpdateCQLResult result) {
									if(result != null){
										if (result.isSuccess()) {
											searchDisplay.getCqlLeftNavBarPanelView().setViewDefinitions(result.getCqlModel().getDefinitionList());
											MatContext.get().setDefinitions(
													getDefinitionList(result.getCqlModel().getDefinitionList()));
											searchDisplay.getCqlLeftNavBarPanelView().clearAndAddDefinitionNamesToListBox();
											searchDisplay.getCqlLeftNavBarPanelView().updateDefineMap();
											searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert().clearAlert();
											searchDisplay.getCqlLeftNavBarPanelView().getSuccessMessageAlert().setVisible(true);

											searchDisplay.getCqlLeftNavBarPanelView().getSearchSuggestDefineTextBox().setText("");
											searchDisplay.getDefineNameTxtArea().setText("");
											searchDisplay.getDefineAceEditor().setText("");
											searchDisplay.getCqlLeftNavBarPanelView().setCurrentSelectedDefinitionObjId(null);
											searchDisplay.getCqlLeftNavBarPanelView().setIsPageDirty(false);
											searchDisplay.getDefineAceEditor().clearAnnotations();
											searchDisplay.getDefineAceEditor().removeAllMarkers();
											searchDisplay.getDefineAceEditor().redisplay();
											searchDisplay.getDefineAceEditor().setAnnotations();
											searchDisplay.getDefineAceEditor().redisplay();
											searchDisplay.getDefineButtonBar().getDeleteButton().setEnabled(false);
											searchDisplay.getCqlLeftNavBarPanelView().getSuccessMessageAlert()
													.createAlert("This Definition has been deleted successfully.");

										} else if (result.getFailureReason() == 2) {
											searchDisplay.getCqlLeftNavBarPanelView().getSuccessMessageAlert().clearAlert();
											searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert()
													.createAlert("Unable to find Node to modify.");
											searchDisplay.getDefineNameTxtArea().setText(definitionName.trim());
										}
									}
									showSearchingBusy(false);
								}
							});
				} else {
					searchDisplay.resetMessageDisplay();
					searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert().createAlert("Please select a definition to delete.");
					searchDisplay.getDefineNameTxtArea().setText(definitionName.trim());
				}
			} else {
				searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert()
						.createAlert(MatContext.get().getMessageDelegate().getERROR_DEFINITION_NAME_NO_SPECIAL_CHAR());
				searchDisplay.getDefineNameTxtArea().setText(definitionName.trim());
			}

		} else {
			searchDisplay.resetMessageDisplay();
			searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert().createAlert("Please select a definition to delete.");
			searchDisplay.getDefineNameTxtArea().setText(definitionName.trim());
		}
	}

	/**
	 * Delete function.
	 */
	protected void deleteFunction() {

		searchDisplay.resetMessageDisplay();
		final String functionName = searchDisplay.getFuncNameTxtArea().getText();
		String functionBody = searchDisplay.getFunctionBodyAceEditor().getText();
		String funcContext = "";
		if (searchDisplay.getContextFuncPATRadioBtn().getValue()) {
			funcContext = "Patient";
		} else {
			funcContext = "Population";
		}
		if (!functionName.isEmpty()) {
			CQLFunctions function = new CQLFunctions();
			function.setFunctionLogic(functionBody);
			function.setFunctionName(functionName);
			function.setArgumentList(searchDisplay.getFunctionArgumentList());
			function.setContext(funcContext);
			if (searchDisplay.getCqlLeftNavBarPanelView().getCurrentSelectedFunctionObjId() != null) {
				CQLFunctions toBeModifiedFuncObj = searchDisplay.getCqlLeftNavBarPanelView().getFunctionMap()
						.get(searchDisplay.getCqlLeftNavBarPanelView().getCurrentSelectedFunctionObjId());
				showSearchingBusy(true);
				MatContext.get().getCQLLibraryService().deleteFunctions(MatContext.get().getCurrentCQLLibraryId(),
						toBeModifiedFuncObj, function, searchDisplay.getCqlLeftNavBarPanelView().getViewFunctions(),
						new AsyncCallback<SaveUpdateCQLResult>() {

							@Override
							public void onFailure(Throwable caught) {
								searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert()
										.createAlert(MatContext.get().getMessageDelegate().getGenericErrorMessage());
								showSearchingBusy(false);
							}

							@Override
							public void onSuccess(SaveUpdateCQLResult result) {
								if(result != null){
									if (result.isSuccess()) {
										searchDisplay.getCqlLeftNavBarPanelView().setViewFunctions(result.getCqlModel().getCqlFunctions());
										MatContext.get().setFuncs(getFunctionList(result.getCqlModel().getCqlFunctions()));
										searchDisplay.getCqlLeftNavBarPanelView().clearAndAddFunctionsNamesToListBox();
										searchDisplay.getCqlLeftNavBarPanelView().updateFunctionMap();
										searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert().clearAlert();

										searchDisplay.getCqlLeftNavBarPanelView().getSearchSuggestFuncTextBox().setText("");
										searchDisplay.getCqlLeftNavBarPanelView().getSuccessMessageAlert().setVisible(true);
										searchDisplay.getFuncNameTxtArea().setText("");
										searchDisplay.getFunctionBodyAceEditor().setText("");
										searchDisplay.getCqlLeftNavBarPanelView().setCurrentSelectedFunctionObjId(null);
										searchDisplay.getCqlLeftNavBarPanelView().setIsPageDirty(false);
										searchDisplay.getFunctionBodyAceEditor().clearAnnotations();
										searchDisplay.getFunctionBodyAceEditor().removeAllMarkers();
										searchDisplay.getFunctionBodyAceEditor().redisplay();
										searchDisplay.getFunctionBodyAceEditor().setAnnotations();
										searchDisplay.getFunctionBodyAceEditor().redisplay();
										searchDisplay.getFunctionButtonBar().getDeleteButton().setEnabled(false);
										searchDisplay.getCqlLeftNavBarPanelView().getSuccessMessageAlert()
												.createAlert("This Function has been deleted successfully.");
									} else if (result.getFailureReason() == 2) {
										searchDisplay.getCqlLeftNavBarPanelView().getSuccessMessageAlert().clearAlert();
										searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert().createAlert("Unable to find Node to modify.");
										searchDisplay.getFuncNameTxtArea().setText(functionName.trim());
									}
									
									if (result.getFunction() != null) {
										searchDisplay
												.createAddArgumentViewForFunctions(result.getFunction().getArgumentList());
									}
								}
								showSearchingBusy(false);
							}
						});
			} else {
				searchDisplay.resetMessageDisplay();
				searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert().createAlert("Please select a function to delete.");
				searchDisplay.getFuncNameTxtArea().setText(functionName.trim());
			}
		} else {
			searchDisplay.resetMessageDisplay();
			searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert().createAlert("Please select a function to delete.");
			searchDisplay.getFuncNameTxtArea().setText(functionName.trim());
		}
	}

	/**
	 * Delete parameter.
	 */
	protected void deleteParameter() {

		searchDisplay.resetMessageDisplay();
		final String parameterName = searchDisplay.getParameterNameTxtArea().getText();
		String parameterBody = searchDisplay.getParameterAceEditor().getText();

		if (!parameterName.isEmpty()) {
			CQLParameter parameter = new CQLParameter();
			parameter.setParameterLogic(parameterBody);
			parameter.setParameterName(parameterName);
			if (searchDisplay.getCqlLeftNavBarPanelView().getCurrentSelectedParamerterObjId() != null) {
				CQLParameter toBeModifiedParamObj = searchDisplay.getCqlLeftNavBarPanelView().getParameterMap()
						.get(searchDisplay.getCqlLeftNavBarPanelView().getCurrentSelectedParamerterObjId());
				showSearchingBusy(true);
				MatContext.get().getCQLLibraryService().deleteParameter(MatContext.get().getCurrentCQLLibraryId(),
						toBeModifiedParamObj, parameter, searchDisplay.getCqlLeftNavBarPanelView().getViewParameterList(),
						new AsyncCallback<SaveUpdateCQLResult>() {

							@Override
							public void onFailure(Throwable caught) {
								searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert()
										.createAlert(MatContext.get().getMessageDelegate().getGenericErrorMessage());
								showSearchingBusy(false);
							}

							@Override
							public void onSuccess(SaveUpdateCQLResult result) {
								if(result != null){
									if (result.isSuccess()) {
										searchDisplay.getCqlLeftNavBarPanelView().setViewParameterList((result.getCqlModel().getCqlParameters()));
										MatContext.get()
												.setParameters(getParamaterList(result.getCqlModel().getCqlParameters()));
										searchDisplay.getCqlLeftNavBarPanelView().clearAndAddParameterNamesToListBox();
										searchDisplay.getCqlLeftNavBarPanelView().updateParamMap();
										searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert().clearAlert();
										searchDisplay.getCqlLeftNavBarPanelView().getSuccessMessageAlert().setVisible(true);

										searchDisplay.getCqlLeftNavBarPanelView().getSearchSuggestParamTextBox().setText("");
										searchDisplay.getParameterNameTxtArea().setText("");
										searchDisplay.getParameterAceEditor().setText("");
										searchDisplay.getCqlLeftNavBarPanelView().setCurrentSelectedParamerterObjId(null);
										searchDisplay.getCqlLeftNavBarPanelView().setIsPageDirty(false);
										searchDisplay.getParameterAceEditor().clearAnnotations();
										searchDisplay.getParameterAceEditor().removeAllMarkers();
										searchDisplay.getParameterAceEditor().redisplay();
										searchDisplay.getParameterAceEditor().setAnnotations();
										searchDisplay.getParameterAceEditor().redisplay();
										searchDisplay.getParameterButtonBar().getDeleteButton().setEnabled(false);
										searchDisplay.getCqlLeftNavBarPanelView().getSuccessMessageAlert()
												.createAlert("This Parameter has been deleted successfully.");
									} else if (result.getFailureReason() == 2) {
										searchDisplay.getCqlLeftNavBarPanelView().getSuccessMessageAlert().clearAlert();
										searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert().createAlert("Unable to find Node to modify.");
										searchDisplay.getParameterNameTxtArea().setText(parameterName.trim());
									}
								}
								showSearchingBusy(false);
							}
						});
			} else {
				searchDisplay.resetMessageDisplay();
				searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert().createAlert("Please select parameter to delete.");
				searchDisplay.getParameterNameTxtArea().setText(parameterName.trim());
			}
		} else {
			searchDisplay.resetMessageDisplay();
			searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert().createAlert("Please select a parameter to delete.");
			searchDisplay.getParameterNameTxtArea().setText(parameterName.trim());
		}
	}
	
	protected void deleteInclude() {

		searchDisplay.resetMessageDisplay();
		final String aliasName = searchDisplay.getIncludeView().getAliasNameTxtArea().getText();
		String includeLibName = searchDisplay.getIncludeView().getCqlLibraryNameTextBox().getText();

		if (!aliasName.isEmpty()) {
			CQLIncludeLibrary cqlLibObject = new CQLIncludeLibrary();
			cqlLibObject.setCqlLibraryName(includeLibName);
			cqlLibObject.setAliasName(aliasName);
			if (searchDisplay.getCqlLeftNavBarPanelView().getCurrentSelectedIncLibraryObjId() != null) {
				CQLIncludeLibrary toBeModifiedIncludeObj = searchDisplay.getCqlLeftNavBarPanelView().getIncludeLibraryMap()
						.get(searchDisplay.getCqlLeftNavBarPanelView().getCurrentSelectedIncLibraryObjId());
				showSearchingBusy(true);
				MatContext.get().getCQLLibraryService().deleteInclude(MatContext.get().getCurrentCQLLibraryId(),
						toBeModifiedIncludeObj, cqlLibObject, searchDisplay.getCqlLeftNavBarPanelView().getViewIncludeLibrarys(),
						new AsyncCallback<SaveUpdateCQLResult>() {

							@Override
							public void onFailure(Throwable caught) {
								searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert()
										.createAlert(MatContext.get().getMessageDelegate().getGenericErrorMessage());
								showSearchingBusy(false);
							}

							@Override
							public void onSuccess(SaveUpdateCQLResult result) {
								if (result.isSuccess()) {
									searchDisplay.getCqlLeftNavBarPanelView().setViewIncludeLibrarys(result.getCqlModel().getCqlIncludeLibrarys());
									MatContext.get().setIncludes(getIncludesList(result.getCqlModel().getCqlIncludeLibrarys()));
									
									searchDisplay.getCqlLeftNavBarPanelView().clearAndAddAliasNamesToListBox();
									searchDisplay.getCqlLeftNavBarPanelView().udpateIncludeLibraryMap();
									searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert().clearAlert();
									searchDisplay.getCqlLeftNavBarPanelView().getSuccessMessageAlert().setVisible(true);

									searchDisplay.getCqlLeftNavBarPanelView().getSearchSuggestIncludeTextBox().setText("");
									searchDisplay.getIncludeView().getAliasNameTxtArea().setText("");
									searchDisplay.getIncludeView().getCqlLibraryNameTextBox().setText("");
									searchDisplay.getIncludeView().getOwnerNameTextBox().setText("");
									searchDisplay.getIncludeView().getViewCQLEditor().setText("");
									searchDisplay.getCqlLeftNavBarPanelView().setCurrentSelectedIncLibraryObjId(null);
									searchDisplay.getCqlLeftNavBarPanelView().setIsPageDirty(false);
									searchDisplay.getIncludeView().getViewCQLEditor().clearAnnotations();
									searchDisplay.getIncludeView().getViewCQLEditor().removeAllMarkers();
									searchDisplay.getIncludeView().getViewCQLEditor().redisplay();
									searchDisplay.getIncludeView().getViewCQLEditor().setAnnotations();
									searchDisplay.getIncludeView().getViewCQLEditor().redisplay();
									searchDisplay.getIncludeView().getDeleteButton().setEnabled(false);
									searchDisplay.getCqlLeftNavBarPanelView().getSuccessMessageAlert()
											.createAlert("This Included Library has been deleted successfully.");
									
									searchDisplay.getIncludeView().getCloseButton().fireEvent(new GwtEvent<ClickHandler>() {
								        @Override
								        public com.google.gwt.event.shared.GwtEvent.Type<ClickHandler> getAssociatedType() {
								        return ClickEvent.getType();
								        }
								        @Override
								        protected void dispatch(ClickHandler handler) {
								            handler.onClick(null);
								        }
								   });
									
								} else if (result.getFailureReason() == 2) {
									searchDisplay.getCqlLeftNavBarPanelView().getSuccessMessageAlert().clearAlert();
									searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert().createAlert("Unable to find Node to modify.");
									searchDisplay.getIncludeView().getAliasNameTxtArea().setText(aliasName.trim());
								}
								showSearchingBusy(false);
							}
						});
			} else {
				searchDisplay.resetMessageDisplay();
				searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert().createAlert("Please select an alias to delete.");
				searchDisplay.getIncludeView().getAliasNameTxtArea().setText(aliasName.trim());
			}
		} else {
			searchDisplay.resetMessageDisplay();
			searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert().createAlert("Please select an alias to delete.");
			searchDisplay.getIncludeView().getAliasNameTxtArea().setText(aliasName.trim());
		}
	}
	
	private void saveCQLGeneralInformation() {
		
		String libraryId = MatContext.get().getCurrentCQLLibraryId();
		String libraryValue = searchDisplay.getCqlGeneralInformationView().getLibraryNameValue().getText().trim();
		showSearchingBusy(true);
		MatContext.get().getCQLLibraryService().saveAndModifyCQLGeneralInfo(libraryId, libraryValue, 
				new AsyncCallback<SaveUpdateCQLResult>() {

					@Override
					public void onFailure(Throwable caught) {
						searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert().createAlert(
								MatContext.get().getMessageDelegate().getGenericErrorMessage());
						showSearchingBusy(false);
					}

					@Override
					public void onSuccess(SaveUpdateCQLResult result) {
						if(result != null) {
							//cqlLibraryName = result.getCqlModel().getLibraryName().trim();
							cqlLibraryName = result.getCqlModel().getLibraryName().trim();
							searchDisplay.getCqlGeneralInformationView().getLibraryNameValue()
							.setText(cqlLibraryName);
							searchDisplay.getCqlLeftNavBarPanelView().getSuccessMessageAlert()
							.createAlert(MatContext.get().getMessageDelegate().getMODIFY_CQL_LIBRARY_NAME());
							MatContext.get().getCurrentLibraryInfo().setLibraryName(cqlLibraryName);
							CqlComposerPresenter.setContentHeading();
						}
						showSearchingBusy(false);
					}
		});
		
	}

	private void logRecentActivity() {
		MatContext.get().getCQLLibraryService().isLibraryAvailableAndLogRecentActivity(MatContext.get().getCurrentCQLLibraryId(), 
				MatContext.get().getLoggedinUserId(), new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onSuccess(Void result) {
						isCQLWorkSpaceLoaded = true;
						displayCQLView();
					}
				});
		
	}
	
	private void displayCQLView(){
		panel.clear();
		
		currentSection = CQLWorkSpaceConstants.CQL_GENERAL_MENU;
		searchDisplay.buildView();
		addLeftNavEventHandler();
		searchDisplay.resetMessageDisplay();
		getCQLData();
		panel.add(searchDisplay.asWidget());
	}
	
	
	
	@Override
	public void beforeClosingDisplay() {
		searchDisplay.getCqlLeftNavBarPanelView().clearShotcutKeyList();
		searchDisplay.getCqlLeftNavBarPanelView().setCurrentSelectedDefinitionObjId(null);
		searchDisplay.getCqlLeftNavBarPanelView().setCurrentSelectedParamerterObjId(null);
		searchDisplay.getCqlLeftNavBarPanelView().setCurrentSelectedFunctionObjId(null);
		searchDisplay.getFunctionArgNameMap().clear();
		searchDisplay.getValueSetView().clearCellTableMainPanel();
		searchDisplay.getIncludeView().getSearchTextBox().setText("");
		searchDisplay.getCqlLeftNavBarPanelView().setIsPageDirty(false);
		searchDisplay.resetMessageDisplay();
		searchDisplay.getCqlLeftNavBarPanelView().getIncludesCollapse().getElement().setClassName("panel-collapse collapse");
		searchDisplay.getCqlLeftNavBarPanelView().getParamCollapse().getElement().setClassName("panel-collapse collapse");
		searchDisplay.getCqlLeftNavBarPanelView().getDefineCollapse().getElement().setClassName("panel-collapse collapse");
		searchDisplay.getCqlLeftNavBarPanelView().getFunctionCollapse().getElement().setClassName("panel-collapse collapse");
		if (searchDisplay.getFunctionArgumentList().size() > 0) {
			searchDisplay.getFunctionArgumentList().clear();
		}
		isModified = false;
		modifyValueSetDTO = null;
		curAceEditor = null;
		currentSection = CQLWorkSpaceConstants.CQL_GENERAL_MENU;
		searchDisplay.getCqlLeftNavBarPanelView().getMessagePanel().clear();
		searchDisplay.resetAll();
		panel.clear();
		searchDisplay.getMainPanel().clear();
		
		
		
	}

	@Override
	public void beforeDisplay() {
		
		if ((MatContext.get().getCurrentCQLLibraryId() == null)
				|| MatContext.get().getCurrentCQLLibraryId().equals("")) {
			displayEmpty();
		} else {
			panel.clear();
			panel.add(searchDisplay.asWidget());
			if (!isCQLWorkSpaceLoaded) { // this check is made so that when CQL
											// is
											// clicked from CQL library, its not
											// called twice.
				
				displayCQLView();
				
			} else {
				isCQLWorkSpaceLoaded = false;
			}
		}
		MatContext.get().getAllCqlKeywordsAndQDMDatatypesForCQLWorkSpaceSA();
		MatContext.get().getAllUnits();
		CqlComposerPresenter.setSubSkipEmbeddedLink("CQLStandaloneWorkSpaceView.containerPanel");
		Mat.focusSkipLists("CqlComposer");
		
	}
	
	private void getCQLData(){
		showSearchingBusy(true);
		MatContext.get().getCQLLibraryService().getCQLData(MatContext.get().getCurrentCQLLibraryId(),  new AsyncCallback<SaveUpdateCQLResult>() {
			
			@Override
			public void onSuccess(SaveUpdateCQLResult result) {
				if(result.isSuccess()){
					if(result.getCqlModel() != null){
						//System.out.println("I got the model");
						
						//if(result.getCqlModel().getLibraryName()!=null){
						if(result.getCqlModel().getLibraryName()!=null){
							cqlLibraryName = searchDisplay.getCqlGeneralInformationView()
									.createCQLLibraryName(MatContext.get().getCurrentCQLLibraryeName());
							searchDisplay.getCqlGeneralInformationView().getLibraryNameValue()
							.setText(cqlLibraryName);
							
							String libraryVersion = MatContext.get().getCurrentCQLLibraryVersion();
							
							libraryVersion = libraryVersion.replaceAll("Draft ", "").trim();
							if(libraryVersion.startsWith("v")){
								libraryVersion = libraryVersion.substring(1);
							}
							
							searchDisplay.getCqlGeneralInformationView().getLibraryVersionValue().setText(libraryVersion);
							
							searchDisplay.getCqlGeneralInformationView().getUsingModelValue().setText("QDM");
							
							searchDisplay.getCqlGeneralInformationView().getModelVersionValue().setText("5.0.2");
						}
						
							List<CQLQualityDataSetDTO> appliedAllValueSetList = new ArrayList<CQLQualityDataSetDTO>();
							List<CQLQualityDataSetDTO> appliedValueSetListInXML = result.getCqlModel()
									.getAllValueSetList();
							
							for (CQLQualityDataSetDTO dto : appliedValueSetListInXML) {
								if (dto.isSuppDataElement())
									continue;
								appliedAllValueSetList.add(dto);
							}
							
							MatContext.get().setValuesets(appliedAllValueSetList);
							searchDisplay.getCqlLeftNavBarPanelView().setAppliedQdmList(appliedAllValueSetList);
							appliedValueSetTableList.clear();
							for (CQLQualityDataSetDTO dto : result.getCqlModel().getValueSetList()) {
								if (dto.isSuppDataElement())
									continue;
								appliedValueSetTableList.add(dto);
							}
							searchDisplay.getCqlLeftNavBarPanelView().setAppliedQdmTableList(appliedValueSetTableList);

							if ((result.getCqlModel().getDefinitionList() != null)
									&& (result.getCqlModel().getDefinitionList().size() > 0)) {
								searchDisplay.getCqlLeftNavBarPanelView().setViewDefinitions(result.getCqlModel().getDefinitionList());
								searchDisplay.getCqlLeftNavBarPanelView().clearAndAddDefinitionNamesToListBox();
								searchDisplay.getCqlLeftNavBarPanelView().updateDefineMap();
								MatContext.get()
										.setDefinitions(getDefinitionList(result.getCqlModel().getDefinitionList()));
							} else {
								searchDisplay.getCqlLeftNavBarPanelView().getDefineBadge().setText("00");
							}
							if ((result.getCqlModel().getCqlParameters() != null)
									&& (result.getCqlModel().getCqlParameters().size() > 0)) {
								searchDisplay.getCqlLeftNavBarPanelView().setViewParameterList(result.getCqlModel().getCqlParameters());
								searchDisplay.getCqlLeftNavBarPanelView().clearAndAddParameterNamesToListBox();
								searchDisplay.getCqlLeftNavBarPanelView().updateParamMap();
								MatContext.get()
										.setParameters(getParamaterList(result.getCqlModel().getCqlParameters()));
							} else {
								searchDisplay.getCqlLeftNavBarPanelView().getParamBadge().setText("00");
							}
							if ((result.getCqlModel().getCqlFunctions() != null)
									&& (result.getCqlModel().getCqlFunctions().size() > 0)) {
								searchDisplay.getCqlLeftNavBarPanelView().setViewFunctions(result.getCqlModel().getCqlFunctions());
								searchDisplay.getCqlLeftNavBarPanelView().clearAndAddFunctionsNamesToListBox();
								searchDisplay.getCqlLeftNavBarPanelView().updateFunctionMap();
								MatContext.get().setFuncs(getFunctionList(result.getCqlModel().getCqlFunctions()));
							} else {
								searchDisplay.getCqlLeftNavBarPanelView().getFunctionBadge().setText("00");
							}
							if ((result.getCqlModel().getCqlIncludeLibrarys() != null)
									&& (result.getCqlModel().getCqlIncludeLibrarys().size() > 0)) {
								searchDisplay.getCqlLeftNavBarPanelView().setViewIncludeLibrarys(result.getCqlModel().getCqlIncludeLibrarys());
								searchDisplay.getCqlLeftNavBarPanelView().clearAndAddAliasNamesToListBox();
								searchDisplay.getCqlLeftNavBarPanelView().udpateIncludeLibraryMap();
								MatContext.get()
										.setIncludes(getIncludesList(result.getCqlModel().getCqlIncludeLibrarys()));
							} else {
								searchDisplay.getCqlLeftNavBarPanelView().getIncludesBadge().setText("00");
								searchDisplay.getCqlLeftNavBarPanelView().getIncludeLibraryMap().clear();
							}

					}
					showSearchingBusy(false);
				}
				
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				showSearchingBusy(false);
			}
		});
	}
	
	
	private void addLeftNavEventHandler() {

		searchDisplay.getCqlLeftNavBarPanelView().getGeneralInformation().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				//searchDisplay.hideAceEditorAutoCompletePopUp();
				generalInfoEvent();
			}
		});
		
		searchDisplay.getCqlLeftNavBarPanelView().getIncludesLibrary().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				searchDisplay.getCqlLeftNavBarPanelView().setIsNavBarClick(true);
				searchDisplay.getCqlLeftNavBarPanelView().setIsDoubleClick(false);
				//searchDisplay.hideAceEditorAutoCompletePopUp();
				if (searchDisplay.getCqlLeftNavBarPanelView().getIsPageDirty()) {
					nextSection = CQLWorkSpaceConstants.CQL_INCLUDES_MENU;
					searchDisplay.getCqlLeftNavBarPanelView().showUnsavedChangesWarning();
					event.stopPropagation();
				} else {
					includesEvent();
				}

			}
		});

	
		
		
		searchDisplay.getCqlLeftNavBarPanelView().getAppliedQDM().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				appliedQDMEvent();

				MatContext.get().getLibraryService().getCQLData(MatContext.get().getCurrentCQLLibraryId(),
						new AsyncCallback<SaveUpdateCQLResult>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert(MatContext.get().getMessageDelegate().getGenericErrorMessage());

					}

					@Override
					public void onSuccess(SaveUpdateCQLResult result) {
                    	String ExpIdentifier = null;
						appliedValueSetTableList.clear();
						List<CQLQualityDataSetDTO> allValuesets = new ArrayList<CQLQualityDataSetDTO>();
						if(result != null){
							for (CQLQualityDataSetDTO dto : result.getCqlModel().getAllValueSetList()) {
								if (dto.isSuppDataElement())
									continue;
								allValuesets.add(dto);
								if(!result.getExpIdentifier().equalsIgnoreCase(""))
									ExpIdentifier = result.getExpIdentifier();
							}
							searchDisplay.getCqlLeftNavBarPanelView().setAppliedQdmList(allValuesets);
							for(CQLQualityDataSetDTO valueset : allValuesets){
								//filtering out codes from valuesets list
								if (valueset.getOid().equals("419099009") || valueset.getOid().equals("21112-8"))
									continue;

								appliedValueSetTableList.add(valueset);		
							}

							searchDisplay.getCqlLeftNavBarPanelView().setAppliedQdmTableList(appliedValueSetTableList);
						}
						searchDisplay.hideAceEditorAutoCompletePopUp();
						appliedQDMEvent();

						//if UMLS is not logged in
						if (!MatContext.get().isUMLSLoggedIn()) {
							if(ExpIdentifier !=null){
								searchDisplay.getValueSetView().getVSACExpansionProfileListBox().setEnabled(false);
								searchDisplay.getValueSetView().getVSACExpansionProfileListBox().clear();
								searchDisplay.getValueSetView().getVSACExpansionProfileListBox().addItem(ExpIdentifier);
								searchDisplay.getValueSetView().getDefaultExpProfileSel().setValue(true);
								searchDisplay.getValueSetView().getDefaultExpProfileSel().setEnabled(false);
								isExpansionProfile = true;
								expProfileToAllValueSet = ExpIdentifier;
							} else {
								expProfileToAllValueSet = "";
								isExpansionProfile = false;
							}
						} else {
							if(ExpIdentifier != null){
								isExpansionProfile = true;
								searchDisplay.getValueSetView().getVSACExpansionProfileListBox().setEnabled(true);
								searchDisplay.getValueSetView().setExpProfileList(MatContext.get()
										.getExpProfileList());
								searchDisplay.getValueSetView().setDefaultExpansionProfileListBox();
								for(int j = 0; j < searchDisplay.getValueSetView().getVSACExpansionProfileListBox().getItemCount(); j++){
									if(searchDisplay.getValueSetView().getVSACExpansionProfileListBox().getItemText(j)
											.equalsIgnoreCase(ExpIdentifier)) {
										searchDisplay.getValueSetView().getVSACExpansionProfileListBox().setItemSelected(j, true);
										searchDisplay.getValueSetView().getVSACExpansionProfileListBox().setSelectedIndex(j);
										searchDisplay.getValueSetView().getDefaultExpProfileSel().setValue(true);
										break;
									}
								}
							} else{
								isExpansionProfile = false;
							}
						}
					}
				});
			}
		});
		searchDisplay.getCqlLeftNavBarPanelView().getParameterLibrary().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				searchDisplay.getCqlLeftNavBarPanelView().setIsNavBarClick(true);
				searchDisplay.getCqlLeftNavBarPanelView().setIsDoubleClick(false);
				searchDisplay.hideAceEditorAutoCompletePopUp();
				if (searchDisplay.getCqlLeftNavBarPanelView().getIsPageDirty()) {
					nextSection = CQLWorkSpaceConstants.CQL_PARAMETER_MENU;
					searchDisplay.getCqlLeftNavBarPanelView().showUnsavedChangesWarning();
					event.stopPropagation();
				} else {
					parameterEvent();
				}

			}
		});

		searchDisplay.getCqlLeftNavBarPanelView().getDefinitionLibrary().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				searchDisplay.getCqlLeftNavBarPanelView().setIsNavBarClick(true);
				searchDisplay.getCqlLeftNavBarPanelView().setIsDoubleClick(false);
				searchDisplay.hideAceEditorAutoCompletePopUp();
				if (searchDisplay.getCqlLeftNavBarPanelView().getIsPageDirty()) {
					nextSection = CQLWorkSpaceConstants.CQL_DEFINE_MENU;
					searchDisplay.getCqlLeftNavBarPanelView().showUnsavedChangesWarning();
					event.stopPropagation();
				} else {
					definitionEvent();
				}
			}
		});

		searchDisplay.getCqlLeftNavBarPanelView().getFunctionLibrary().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				searchDisplay.getCqlLeftNavBarPanelView().setIsNavBarClick(true);
				searchDisplay.getCqlLeftNavBarPanelView().setIsDoubleClick(false);
				searchDisplay.hideAceEditorAutoCompletePopUp();
				if (searchDisplay.getCqlLeftNavBarPanelView().getIsPageDirty()) {
					nextSection = CQLWorkSpaceConstants.CQL_FUNCTION_MENU;
					searchDisplay.getCqlLeftNavBarPanelView().showUnsavedChangesWarning();
					event.stopPropagation();
				} else {
					functionEvent();
				}
			}
		});

		searchDisplay.getCqlLeftNavBarPanelView().getViewCQL().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				searchDisplay.hideAceEditorAutoCompletePopUp();
				viewCqlEvent();
			}
		});

	}
	
	/**
	 * Adds the QDM Search Panel event Handlers.
	 */
	private void addValueSetEventHandlers() {
		searchDisplay.getValueSetView().getApplyDefaultExpansionIdButton().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (MatContext.get().getLibraryLockService().checkForEditPermission()) {
					// code for adding profile to List to applied QDM
					searchDisplay.resetMessageDisplay();
					if (!MatContext.get().isUMLSLoggedIn()) { // UMLS
						// Login
						// Validation
						searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert()
								.createAlert(MatContext.get().getMessageDelegate().getUMLS_NOT_LOGGEDIN());
						searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert().setVisible(true);
						return;
					}
					int selectedindex = searchDisplay.getValueSetView().getVSACExpansionProfileListBox().getSelectedIndex();
					String selectedValue =
							searchDisplay.getValueSetView().getVSACExpansionProfileListBox().getValue(selectedindex);

					if(!selectedValue.equalsIgnoreCase("--Select--")){
						isExpansionProfile = true;
						expProfileToAllValueSet = selectedValue;
						updateAllValueSetWithExpProfile(appliedValueSetTableList);
						} 
					else if(!searchDisplay.getValueSetView().getDefaultExpProfileSel().getValue()){ 
						isExpansionProfile = false;
						expProfileToAllValueSet = "";
						updateAllValueSetWithExpProfile(appliedValueSetTableList);
						} else {
							searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert()
							.createAlert(MatContext.get()
									.getMessageDelegate().getVsacExpansionProfileSelection
									());}
				}
			}
		});

		searchDisplay.getValueSetView().getDefaultExpProfileSel()
				.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
					@Override
					public void onValueChange(ValueChangeEvent<Boolean> event) {
						if (event.getValue().toString().equals("true")) {
							if (!MatContext.get().isUMLSLoggedIn()) { // UMLS
								// Login
								// Validation
								searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert()
										.createAlert(MatContext.get().getMessageDelegate().getUMLS_NOT_LOGGEDIN());
								searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert().setVisible(true);
								return;
							}
							searchDisplay.getValueSetView().getVSACExpansionProfileListBox().setEnabled(true);
							searchDisplay.getValueSetView().setExpProfileList(MatContext.get().getExpProfileList());
							searchDisplay.getValueSetView().setDefaultExpansionProfileListBox();
						} else if (event.getValue().toString().equals("false")) {
							searchDisplay.getValueSetView().getVSACExpansionProfileListBox().setEnabled(false);
							searchDisplay.getValueSetView().setDefaultExpansionProfileListBox();
						}

					}
				});
		/**
		 * this functionality is to clear the content on the QDM Element Search
		 * Panel.
		 */
		searchDisplay.getValueSetView().getCancelQDMButton().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				searchDisplay.resetMessageDisplay();
				isModified = false;
				searchDisplay.getValueSetView().resetCQLValuesetearchPanel();
			}
		});

		searchDisplay.getValueSetView().getUpdateFromVSACButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				if(MatContext.get().getLibraryLockService().checkForEditPermission()){
					searchDisplay.resetMessageDisplay();
					updateVSACValueSets();	
				}
			}
		});
	
		/**
		 * this functionality is to retrieve the value set from VSAC with latest
		 * information which consists of Expansion Profile list and Version
		 * List.
		 */
		searchDisplay.getValueSetView().getRetrieveFromVSACButton().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (MatContext.get().getLibraryLockService().checkForEditPermission()) {
					searchDisplay.resetMessageDisplay();
					String version = null;
					String expansionProfile = null;
					searchValueSetInVsac(version, expansionProfile);
				}
			}
		});

		/**
		 * this handler is invoked when apply button is clicked on search Panel
		 * in QDM elements tab and this is to add new value set or user Defined
		 * QDM to the Applied QDM list.
		 */
		searchDisplay.getValueSetView().getSaveButton().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (MatContext.get().getLibraryLockService().checkForEditPermission()) {
					MatContext.get().clearDVIMessages();
					searchDisplay.resetMessageDisplay();
					
					if (isModified && (modifyValueSetDTO != null)) {
						 modifyValueSetOrUserDefined(isUserDefined);
					} else {
						addNewValueSet(isUserDefined);
					}
				}
			}
		});

		/**
		 * Adding value Change handler for UserDefined Input in Search Panel in
		 * QDM Elements Tab
		 * 
		 */
		searchDisplay.getValueSetView().getUserDefinedInput().addValueChangeHandler(new ValueChangeHandler<String>() {

			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				searchDisplay.resetMessageDisplay();
				isUserDefined = searchDisplay.getValueSetView().validateUserDefinedInput(isUserDefined);
			}
		});

		/**
		 * Adding value change handler for OID input in Search Panel in QDM
		 * elements Tab
		 */

		searchDisplay.getValueSetView().getOIDInput().addValueChangeHandler(new ValueChangeHandler<String>() {

			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				searchDisplay.resetMessageDisplay();
				isUserDefined = searchDisplay.getValueSetView().validateOIDInput(isUserDefined);
			}
		});

		/**
		 * value change handler for Expansion Profile in Search Panel in QDM
		 * Elements Tab
		 */
		searchDisplay.getValueSetView().getQDMExpProfileListBox().addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				// TODO Auto-generated method stub
				searchDisplay.resetMessageDisplay();
				if (!searchDisplay.getValueSetView()
						.getExpansionProfileValue(searchDisplay.getValueSetView().getQDMExpProfileListBox())
						.equalsIgnoreCase(MatContext.PLEASE_SELECT)) {
					searchDisplay.getValueSetView().getVersionListBox().setSelectedIndex(0);
				}
			}
		});

		/**
		 * value Change Handler for Version listBox in Search Panel
		 */
		searchDisplay.getValueSetView().getVersionListBox().addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				searchDisplay.resetMessageDisplay();
				if (!searchDisplay.getValueSetView().getVersionValue(searchDisplay.getValueSetView().getVersionListBox())
						.equalsIgnoreCase(MatContext.PLEASE_SELECT)) {
					searchDisplay.getValueSetView().getQDMExpProfileListBox().setSelectedIndex(0);
				}

			}
		});
		
		searchDisplay.getValueSetView().setObserver(new CQLAppliedValueSetView.Observer() {
			@Override
			public void onModifyClicked(CQLQualityDataSetDTO result) {
				searchDisplay.resetMessageDisplay();
				searchDisplay.getValueSetView().resetCQLValuesetearchPanel();
				isModified = true;
				modifyValueSetDTO = result;
				String displayName = result.getCodeListName();
				HTML searchHeaderText = new HTML("<strong>Modify value set ( "+displayName +")</strong>");
				searchDisplay.getValueSetView().getSearchHeader().clear();
				searchDisplay.getValueSetView().getSearchHeader().add(searchHeaderText);
				searchDisplay.getValueSetView().getMainPanel().getElement().focus();
				if(result.getOid().equalsIgnoreCase(ConstantMessages.USER_DEFINED_QDM_OID)){
					isUserDefined = true;
				} else {
					isUserDefined = false;
				}
				
				onModifyValueSet(result, isUserDefined);

			}

			@Override
			public void onDeleteClicked(CQLQualityDataSetDTO result, final int index) {
				searchDisplay.resetMessageDisplay();
				searchDisplay.getValueSetView().resetCQLValuesetearchPanel();
				if((modifyValueSetDTO!=null) && modifyValueSetDTO.getId().equalsIgnoreCase(result.getId())){
					isModified = false;
				}
				String libraryId = MatContext.get().getCurrentCQLLibraryId();
				if ((libraryId != null) && !libraryId.equals("")) {
					showSearchingBusy(true);
					MatContext.get().getLibraryService().getCQLData(libraryId, new AsyncCallback<SaveUpdateCQLResult>() {
						
						@Override
						public void onSuccess(final SaveUpdateCQLResult result) {
							appliedValueSetTableList.clear();
							if (result.getCqlModel().getAllValueSetList() != null) {
								for (CQLQualityDataSetDTO dto : result.getCqlModel().getAllValueSetList()) {
									if(dto.isSuppDataElement())
										continue;
									appliedValueSetTableList.add(dto);
								}
								
								if (appliedValueSetTableList.size() > 0) {
									Iterator<CQLQualityDataSetDTO> iterator = appliedValueSetTableList.iterator();
									while (iterator.hasNext()) {
										CQLQualityDataSetDTO dataSetDTO = iterator
												.next();
										if(dataSetDTO
												.getUuid() != null){
											if (dataSetDTO
													.getUuid()
													.equals(searchDisplay.getValueSetView()
															.getSelectedElementToRemove()
															.getUuid())) {
												if(!dataSetDTO.isUsed()){
													deleteValueSet(dataSetDTO.getId());
													iterator.remove();
												}
											}
										}
									}
								}
							}
							showSearchingBusy(false);
						}
						
						@Override
						public void onFailure(Throwable caught) {
							showSearchingBusy(false);
							Window.alert(MatContext.get().getMessageDelegate().getGenericErrorMessage());
						}
					});
				}
			}
			
			
		});
	}
	
	/**
	 * Update vsac value sets.
	 */
	private void updateVSACValueSets() {
		showSearchingBusy(true);
		String expansionId = null;
		if(expProfileToAllValueSet.isEmpty()){
			expansionId = null;
		} else {
			expansionId = expProfileToAllValueSet;
		}
		cqlService.updateCQLVSACValueSets(MatContext.get().getCurrentCQLLibraryId(), expansionId,
				new AsyncCallback<VsacApiResult>() {
			
			@Override
			public void onFailure(final Throwable caught) {
				Window.alert(MatContext.get().getMessageDelegate().getGenericErrorMessage());
				showSearchingBusy(false);
			}
			
			@Override
			public void onSuccess(final VsacApiResult result) {
				if (result.isSuccess()) {
					searchDisplay.getCqlLeftNavBarPanelView().getSuccessMessageAlert().createAlert(MatContext.get().getMessageDelegate().getVSAC_UPDATE_SUCCESSFULL());
					List<CQLQualityDataSetDTO> appliedListModel = new ArrayList<CQLQualityDataSetDTO>();
					for (CQLQualityDataSetDTO cqlQDMDTO : result.getUpdatedCQLQualityDataDTOLIst()) {
						if (!ConstantMessages.EXPIRED_OID.equals(cqlQDMDTO
								.getDataType()) && !ConstantMessages.BIRTHDATE_OID.equals(cqlQDMDTO
										.getDataType()))  {
							appliedListModel.add(cqlQDMDTO);
						} 
					}
					searchDisplay.getValueSetView().buildAppliedValueSetCellTable(appliedListModel, MatContext.get().getLibraryLockService().checkForEditPermission());
				} else {
					searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert().createAlert(searchDisplay.getValueSetView().convertMessage(result.getFailureReason()));
				}
				showSearchingBusy(false);
			}
		});
	}
	
	
	
	/**
	 * Gets the profile list.
	 *
	 * @param list
	 *            the list
	 * @return the profile list
	 */
	private List<? extends HasListBox> getProfileList(List<VSACExpansionProfile> list) {
		return list;
	}
	
	/**
	 * Gets the version list.
	 *
	 * @param list
	 *            the list
	 * @return the version list
	 */
	private List<? extends HasListBox> getVersionList(List<VSACVersion> list) {
		return list;
	}

	/**
	 * Gets the VSAC version list by oid. if the default Expansion Profile is
	 * present then we are not making this VSAC call.
	 * 
	 * @param oid
	 *            the oid
	 * @return the VSAC version list by oid
	 */
	private void getVSACVersionListByOID(String oid) {
		vsacapiService.getAllVersionListByOID(oid, new AsyncCallback<VsacApiResult>() {

			@Override
			public void onSuccess(VsacApiResult result) {

				if (result.getVsacVersionResp() != null) {
					searchDisplay.getValueSetView().setQDMVersionListBoxOptions(getVersionList(result.getVsacVersionResp()));
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert()
						.createAlert(MatContext.get().getMessageDelegate().getVSAC_RETRIEVE_FAILED());
				searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert().setVisible(true);
				// showSearchingBusy(false);
			}
		});

	}
	
	/**
	 * Search value set in vsac.
	 *
	 * @param version
	 *            the version
	 * @param expansionProfile
	 *            the expansion profile
	 */
	private void searchValueSetInVsac(String version, String expansionProfile) {

		final String oid = searchDisplay.getValueSetView().getOIDInput().getValue();
		if (!MatContext.get().isUMLSLoggedIn()) {
			searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert().createAlert(MatContext.get().getMessageDelegate().getUMLS_NOT_LOGGEDIN());
			searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert().setVisible(true);

			return;
		}
		
		// OID validation.
		if ((oid == null) || oid.trim().isEmpty()) {
			searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert().createAlert(MatContext.get().getMessageDelegate().getUMLS_OID_REQUIRED());
			searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert().setVisible(true);
			return;
		}
		showSearchingBusy(true);
		expProfileToAllValueSet = getExpProfileValue();
		if (expProfileToAllValueSet.isEmpty()) {
			isExpansionProfile = false;
			expansionProfile = null;
		} else {
			isExpansionProfile = true;
			expansionProfile = expProfileToAllValueSet;
		}

		vsacapiService.getMostRecentValueSetByOID(oid, expansionProfile, new AsyncCallback<VsacApiResult>() {

			@Override
			public void onFailure(final Throwable caught) {
				searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert()
						.createAlert(MatContext.get().getMessageDelegate().getVSAC_RETRIEVE_FAILED());
				searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert().setVisible(true);
				showSearchingBusy(false);
			}

			/**
			 * On success.
			 * 
			 * @param result
			 *            the result
			 */
			@Override
			public void onSuccess(final VsacApiResult result) {
				// to get the VSAC version list corresponding the OID
				if (result.isSuccess()) {
					List<MatValueSet> matValueSets = result.getVsacResponse();
					if (matValueSets != null) {
						MatValueSet matValueSet = matValueSets.get(0);
						currentMatValueSet = matValueSet;
					}
					searchDisplay.getValueSetView().getOIDInput().setTitle(oid);
					searchDisplay.getValueSetView().getUserDefinedInput().setValue(matValueSets.get(0).getDisplayName());
					searchDisplay.getValueSetView().getUserDefinedInput().setTitle(matValueSets.get(0).getDisplayName());
					searchDisplay.getValueSetView().getQDMExpProfileListBox().setEnabled(true);
					searchDisplay.getValueSetView().getVersionListBox().setEnabled(true);

					searchDisplay.getValueSetView().getSaveButton().setEnabled(true);

					if (isExpansionProfile) {
						searchDisplay.getValueSetView().getQDMExpProfileListBox().setEnabled(false);
						searchDisplay.getValueSetView().getVersionListBox().setEnabled(false);
						searchDisplay.getValueSetView().getQDMExpProfileListBox().clear();
						searchDisplay.getValueSetView().getQDMExpProfileListBox().addItem(expProfileToAllValueSet,
								expProfileToAllValueSet);
					} else {
						searchDisplay.getValueSetView().setQDMExpProfileListBox(
								getProfileList(MatContext.get().getVsacExpProfileList()));
						getVSACVersionListByOID(oid);
						searchDisplay.getValueSetView().getQDMExpProfileListBox().setEnabled(true);
						searchDisplay.getValueSetView().getVersionListBox().setEnabled(true);
					}
					showSearchingBusy(false);
					searchDisplay.getCqlLeftNavBarPanelView().getSuccessMessageAlert()
							.createAlert(MatContext.get().getMessageDelegate().getVSAC_RETRIEVAL_SUCCESS());
					searchDisplay.getCqlLeftNavBarPanelView().getSuccessMessageAlert().setVisible(true);

				} else {
					String message = searchDisplay.getValueSetView().convertMessage(result.getFailureReason());
					searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert().createAlert(message);
					searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert().setVisible(true);
					showSearchingBusy(false);
				}
			}
		});
	}
	
	/**
	 * Modify QDM.
	 *
	 * @param isUserDefined the is user defined
	 */
	protected final void modifyValueSetOrUserDefined(final boolean isUserDefined) {
		if (!isUserDefined) { //Normal Available Value Set Flow
			modifyValueSet();
		} else { //Pseudo Value set Flow
			modifyUserDefinedValueSet();
		}
	}

	
	/**
	 * Modify value set QDM.
	 */
	private void modifyValueSet() {
		//Normal Available QDM Flow
		MatValueSet modifyWithDTO = currentMatValueSet;
		if ((modifyValueSetDTO != null) && (modifyWithDTO != null)) {
			String expansionId;
			String version;
			String displayName = searchDisplay.getValueSetView().getUserDefinedInput().getText();
			expansionId = searchDisplay.getValueSetView().getExpansionProfileValue(searchDisplay.getValueSetView().getQDMExpProfileListBox());
			version = searchDisplay.getValueSetView().getVersionValue(searchDisplay.getValueSetView().getVersionListBox());
			if(expansionId == null){
				expansionId = "";
			}
			if(version == null){
				version = "";
			}
			expProfileToAllValueSet = getExpProfileValue();
			if(modifyValueSetDTO.getExpansionIdentifier() == null){
				if(expProfileToAllValueSet.equalsIgnoreCase("")){
					modifyValueSetDTO.setExpansionIdentifier("");
				} else {
					modifyValueSetDTO.setExpansionIdentifier(expProfileToAllValueSet);
				}
			}
			if(modifyValueSetDTO.getVersion() == null){
				modifyValueSetDTO.setVersion("");
			}
			
			modifyValueSetList(modifyValueSetDTO);
			
			if(!CheckNameInValueSetList(displayName)){
				updateAppliedValueSetsList(modifyWithDTO, null, modifyValueSetDTO, false);
			}
		} else {
			searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert().createAlert(MatContext.get().
					getMessageDelegate().getMODIFY_VALUE_SET_SELECT_ATLEAST_ONE());
		}
	}
	
	/**
	 * Modify QDM list.
	 *
	 * @param qualityDataSetDTO the quality data set DTO
	 */
	private void modifyValueSetList(CQLQualityDataSetDTO qualityDataSetDTO) {
		for (int i = 0; i < appliedValueSetTableList.size(); i++) {
			if (qualityDataSetDTO.getCodeListName().equals(appliedValueSetTableList.get(i).getCodeListName())) {
				appliedValueSetTableList.remove(i);
				break;
				
			}
		}
	}
	
	/**
	 * Update applied Value Set list.
	 *
	 * @param matValueSet the mat value set
	 * @param codeListSearchDTO the code list search DTO
	 * @param qualityDataSetDTO the quality data set DTO
	 * @param isUSerDefined the is U ser defined
	 */
	private void updateAppliedValueSetsList(final MatValueSet matValueSet , final CodeListSearchDTO codeListSearchDTO ,
			final CQLQualityDataSetDTO qualityDataSetDTO, final boolean isUSerDefined) {
		
		//modifyQDMList(qualityDataSetDTO);
		String version = searchDisplay.getValueSetView().getVersionValue(searchDisplay.getValueSetView().getVersionListBox());
		String expansionProfile = searchDisplay.getValueSetView().getExpansionProfileValue(
				searchDisplay.getValueSetView().getQDMExpProfileListBox());
		CQLValueSetTransferObject matValueSetTransferObject = new CQLValueSetTransferObject();
		matValueSetTransferObject.setCqlLibraryId(MatContext.get().getCurrentCQLLibraryId());
		matValueSetTransferObject.setMatValueSet(matValueSet);
		matValueSetTransferObject.setCodeListSearchDTO(codeListSearchDTO);
		matValueSetTransferObject.setCqlQualityDataSetDTO(qualityDataSetDTO);
		matValueSetTransferObject.setAppliedQDMList(appliedValueSetTableList);
		int expIdselectedIndex = searchDisplay.getValueSetView().getQDMExpProfileListBox().getSelectedIndex();
		int versionSelectionIndex = searchDisplay.getValueSetView().getVersionListBox().getSelectedIndex();
		if((version != null) || (expansionProfile != null) ){
			if (!expansionProfile.equalsIgnoreCase(MatContext.PLEASE_SELECT)
					&& !expansionProfile.equalsIgnoreCase("")) {
				matValueSetTransferObject.setExpansionProfile(true);
				matValueSetTransferObject.setVersion(false);
				currentMatValueSet.setExpansionProfile(searchDisplay
						.getValueSetView().getQDMExpProfileListBox().getValue(expIdselectedIndex));
				
			} else if (!version.equalsIgnoreCase(MatContext.PLEASE_SELECT)
					&& !version.equalsIgnoreCase("")){
				matValueSetTransferObject.setVersion(true);
				matValueSetTransferObject.setExpansionProfile(false);
				currentMatValueSet.setVersion(searchDisplay.getValueSetView().getVersionListBox().getValue(versionSelectionIndex));
			}
		}
		
		if(!expProfileToAllValueSet.isEmpty() && !isUSerDefined){
			currentMatValueSet.setExpansionProfile(expProfileToAllValueSet);
			currentMatValueSet.setVersion("1.0");
			matValueSetTransferObject.setExpansionProfile(true);
			matValueSetTransferObject.setVersion(false);
		}
		matValueSetTransferObject.scrubForMarkUp();
		showSearchingBusy(true);
		MatContext.get().getLibraryService().modifyCQLValueSets(matValueSetTransferObject,
				new AsyncCallback<SaveUpdateCQLResult>() {
			@Override
			public void onFailure(final Throwable caught) {
				searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert().createAlert(
							MatContext.get().getMessageDelegate().getGenericErrorMessage());
				showSearchingBusy(false);
		
			}
			@Override
			public void onSuccess(final SaveUpdateCQLResult result) {
				if(result != null){
					if(result.isSuccess()){
						isModified = false;
						searchDisplay.getValueSetView().resetCQLValuesetearchPanel();
						modifyValueSetDTO = result.getCqlQualityDataSetDTO();
						searchDisplay.getCqlLeftNavBarPanelView().getSuccessMessageAlert()
						.createAlert(MatContext.get().getMessageDelegate().getSUCCESSFUL_MODIFY_APPLIED_VALUESET());
						getAppliedValueSetList();
					} else{
						
						if (result.getFailureReason() == SaveUpdateCodeListResult.ALREADY_EXISTS) {
							searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert().createAlert(
										MatContext.get().getMessageDelegate().getDuplicateAppliedValueSetMsg());
						
						} else if (result.getFailureReason() == SaveUpdateCodeListResult.SERVER_SIDE_VALIDATION) {
							searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert().createAlert("Invalid Input data.");
						}
					}
				}
				showSearchingBusy(false);
			}
		});
		
	}
	
	/**
	 * Check name in QDM list.
	 *
	 * @param userDefinedInput the user defined input
	 * @return true, if successful
	 */
	private boolean CheckNameInValueSetList(String userDefinedInput) {
		if (appliedValueSetTableList.size() > 0) {
			Iterator<CQLQualityDataSetDTO> iterator = appliedValueSetTableList.iterator();
			while (iterator.hasNext()) {
				CQLQualityDataSetDTO dataSetDTO = iterator.next();
				if (dataSetDTO.getCodeListName().equalsIgnoreCase(userDefinedInput)) {
					searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert()
							.createAlert(MatContext.get().getMessageDelegate().getDuplicateAppliedValueSetMsg());
					return true;
					
				}
			}
		}
		return false;
	}

	/**
	 * Adds the selected code listto library.
	 *
	 * @param isUserDefinedValueSet the is user defined QDM
	 */
	private void addNewValueSet(final boolean isUserDefinedValueSet) {
		if (!isUserDefinedValueSet) {
			addVSACCQLValueset();
		} else {
			addUserDefinedValueSet();
		}
	}
	
	/**
	 * Adds the QDS with value set.
	 */
	private void addVSACCQLValueset() {

		String libraryID = MatContext.get().getCurrentCQLLibraryId();
		CQLValueSetTransferObject matValueSetTransferObject = createValueSetTransferObject(libraryID);
		matValueSetTransferObject.scrubForMarkUp();
		final String codeListName = matValueSetTransferObject.getMatValueSet().getDisplayName();
		String expProfile = matValueSetTransferObject.getMatValueSet().getExpansionProfile();
		String version = matValueSetTransferObject.getMatValueSet().getVersion();
		expProfileToAllValueSet = getExpProfileValue();
		if(!expProfileToAllValueSet.equalsIgnoreCase("")){
			expProfile = expProfileToAllValueSet;
			matValueSetTransferObject.getMatValueSet().setExpansionProfile(expProfile);
		}
		if(expProfile == null){
			expProfile = "";
			matValueSetTransferObject.getMatValueSet().setExpansionProfile(expProfile);
		}
		if (version == null) {
			version = "";
		}
		// Check if QDM name already exists in the list.
		if (!CheckNameInValueSetList(codeListName)) {
			showSearchingBusy(true);
			MatContext.get().getLibraryService().saveCQLValueset(matValueSetTransferObject,
					new AsyncCallback<SaveUpdateCQLResult>() {

						@Override
						public void onFailure(Throwable caught) {
							showSearchingBusy(false);
							if (appliedValueSetTableList.size() > 0) {
								appliedValueSetTableList.removeAll(appliedValueSetTableList);
							}
						}

						@Override
						public void onSuccess(SaveUpdateCQLResult result) {
							String message = "";
							if(result != null){
								if (result.isSuccess()) {
									
									message = MatContext.get().getMessageDelegate().getValuesetSuccessMessage(codeListName);
									MatContext.get().getEventBus().fireEvent(new QDSElementCreatedEvent(codeListName));
									searchDisplay.getValueSetView().resetCQLValuesetearchPanel();
									searchDisplay.getCqlLeftNavBarPanelView().getSuccessMessageAlert().createAlert(message);
									getAppliedValueSetList();
								} else {
									if (result.getFailureReason() == SaveUpdateCodeListResult.ALREADY_EXISTS) {
										searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert().createAlert(
												MatContext.get().getMessageDelegate().getDuplicateAppliedValueSetMsg());
									}
								}
							}
							showSearchingBusy(false);
						}
					});
		}

	}
	
	/**
	 * Adds the QDS with out value set.
	 */
	private void addUserDefinedValueSet() {

		CQLValueSetTransferObject matValueSetTransferObject = createValueSetTransferObject(
				MatContext.get().getCurrentCQLLibraryId());
		matValueSetTransferObject.scrubForMarkUp();

		if ((matValueSetTransferObject.getUserDefinedText().length() > 0)) {
			ValueSetNameInputValidator valueSetNameInputValidator = new ValueSetNameInputValidator();
			String message = valueSetNameInputValidator.validate(matValueSetTransferObject);
			if (message.isEmpty()) {
				final String userDefinedInput = matValueSetTransferObject.getUserDefinedText();
				String expProfile = searchDisplay.getValueSetView()
						.getExpansionProfileValue(searchDisplay.getValueSetView().getQDMExpProfileListBox());
				String version = searchDisplay.getValueSetView()
						.getVersionValue(searchDisplay.getValueSetView().getVersionListBox());
				if (expProfile == null) {
					expProfile = "";
				}
				if (version == null) {
					version = "";
				}
				// Check if QDM name already exists in the list.
				if (!CheckNameInValueSetList(userDefinedInput)) {
					showSearchingBusy(true);
					MatContext.get().getLibraryService().saveCQLUserDefinedValueset(matValueSetTransferObject,
							new AsyncCallback<SaveUpdateCQLResult>() {
								@Override
								public void onFailure(final Throwable caught) {
									Window.alert(MatContext.get().getMessageDelegate().getGenericErrorMessage());
									showSearchingBusy(false);
								}

								@SuppressWarnings("static-access")
								@Override
								public void onSuccess(final SaveUpdateCQLResult result) {
									if(result != null){
										if (result.isSuccess()) {
											if (result.getXml() != null) {
												
												String message = MatContext.get().getMessageDelegate()
														.getValuesetSuccessMessage(userDefinedInput);
												searchDisplay.getCqlLeftNavBarPanelView().getSuccessMessageAlert().createAlert(message);
												MatContext.get().setValuesets(result.getCqlAppliedQDMList());
												searchDisplay.getValueSetView().resetCQLValuesetearchPanel();
												getAppliedValueSetList();
											}
										} else {
											if (result.getFailureReason() == result.ALREADY_EXISTS) {
												searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert().createAlert(
														MatContext.get().getMessageDelegate().getDuplicateAppliedValueSetMsg());
											} else if (result.getFailureReason() == result.SERVER_SIDE_VALIDATION) {
												searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert().createAlert("Invalid input data.");
											}
										}
									}
									showSearchingBusy(false);
								}
							});

				}
			} else {
				searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert().createAlert(message);
			}

		} else {
			searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert()
					.createAlert(MatContext.get().getMessageDelegate().getVALIDATION_MSG_ELEMENT_WITHOUT_VSAC());
		}

	}
	
	/**
	 * Creates the value set transfer object.
	 *
	 * @param libraryID the library ID
	 * @return the CQL value set transfer object
	 */
	private CQLValueSetTransferObject createValueSetTransferObject(String libraryID) {
		String version = searchDisplay.getValueSetView().getVersionValue(searchDisplay.getValueSetView().getVersionListBox());
		String expansionProfile = searchDisplay.getValueSetView().getExpansionProfileValue(
				searchDisplay.getValueSetView().getQDMExpProfileListBox());
		int expIdSelectionIndex = searchDisplay.getValueSetView().getQDMExpProfileListBox().getSelectedIndex();
		int versionSelectionIndex = searchDisplay.getValueSetView().getVersionListBox().getSelectedIndex();
		
		CQLValueSetTransferObject matValueSetTransferObject = new CQLValueSetTransferObject();
		matValueSetTransferObject.setCqlLibraryId(libraryID);
		CodeListSearchDTO codeListSearchDTO = new CodeListSearchDTO();
		codeListSearchDTO.setName(searchDisplay.getValueSetView().getUserDefinedInput().getText());
		matValueSetTransferObject.setCodeListSearchDTO(codeListSearchDTO);
		matValueSetTransferObject.setAppliedQDMList(appliedValueSetTableList);
		if((version != null) || (expansionProfile != null) ){
			if (!expansionProfile.equalsIgnoreCase(MatContext.PLEASE_SELECT)
					&& !expansionProfile.equalsIgnoreCase("")) {
				matValueSetTransferObject.setExpansionProfile(true);
				matValueSetTransferObject.setVersion(false);
				currentMatValueSet.setExpansionProfile(searchDisplay
						.getValueSetView().getQDMExpProfileListBox().getValue(expIdSelectionIndex));
				
			} else if (!version.equalsIgnoreCase(MatContext.PLEASE_SELECT)
					&& !version.equalsIgnoreCase("")){
				matValueSetTransferObject.setVersion(true);
				matValueSetTransferObject.setExpansionProfile(false);
				currentMatValueSet.setVersion(searchDisplay.getValueSetView().getVersionListBox().getValue(versionSelectionIndex));
			}
		}
		
		
		if (!expProfileToAllValueSet.isEmpty() && !isUserDefined) {
			currentMatValueSet.setExpansionProfile(expProfileToAllValueSet);
			matValueSetTransferObject.setExpansionProfile(true);
			matValueSetTransferObject.setVersion(false);
		}
		matValueSetTransferObject.setMatValueSet(currentMatValueSet);
		matValueSetTransferObject.setCqlLibraryId(libraryID);
		matValueSetTransferObject.setUserDefinedText(searchDisplay.getValueSetView().getUserDefinedInput().getText());
		return matValueSetTransferObject;
	}
	
	/**
	 * Modify QDM with out value set.
	 */
	private void modifyUserDefinedValueSet() {
		modifyValueSetDTO.setExpansionIdentifier("");
		modifyValueSetDTO.setVersion("");
		if ((searchDisplay.getValueSetView().getUserDefinedInput().getText().trim().length() > 0)) {
			final String usrDefDisplayName = searchDisplay.getValueSetView().getUserDefinedInput().getText();
			String expProfile = searchDisplay.getValueSetView().getExpansionProfileValue(searchDisplay.getValueSetView().getQDMExpProfileListBox());
			String version = searchDisplay.getValueSetView().getVersionValue(searchDisplay.getValueSetView().getVersionListBox());
			if(expProfile == null){
				expProfile = "";
			}
			if(version == null){
				version = "";
			}
			
			modifyValueSetList(modifyValueSetDTO);
			if(!CheckNameInValueSetList(usrDefDisplayName)){
				CQLValueSetTransferObject object = new CQLValueSetTransferObject();
				object.setUserDefinedText(searchDisplay.getValueSetView().getUserDefinedInput().getText());
				object.scrubForMarkUp();
				ValueSetNameInputValidator valueSetNameInputValidator = new ValueSetNameInputValidator();
				/*qdmInputValidator.validate(object);*/
				String message = valueSetNameInputValidator.validate(object);
				if (message.isEmpty()) {
				
					CodeListSearchDTO modifyWithDTO = new CodeListSearchDTO();
					modifyWithDTO.setName(searchDisplay.getValueSetView().getUserDefinedInput().getText());
					updateAppliedValueSetsList(null, modifyWithDTO, modifyValueSetDTO, true);
				} else {
					searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert().createAlert(message);
				}
			}
		} else {
			searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert().createAlert(
					MatContext.get().getMessageDelegate().getVALIDATION_MSG_ELEMENT_WITHOUT_VSAC());
		}
	}
	
		
	/**
	 * Update all applied QDM Elements with default Expansion Profile.
	 *
	 * @param list the list
	 */
	private void updateAllValueSetWithExpProfile(List<CQLQualityDataSetDTO> list) {
		List<CQLQualityDataSetDTO> modifiedCqlValueSetList = new ArrayList<CQLQualityDataSetDTO>();
		for (CQLQualityDataSetDTO cqlQualityDataSetDTO : list) {
			if (!ConstantMessages.USER_DEFINED_QDM_OID.equalsIgnoreCase(cqlQualityDataSetDTO.getOid())) {
				cqlQualityDataSetDTO.setVersion("1.0");
				if (!expProfileToAllValueSet.isEmpty()) {
					cqlQualityDataSetDTO.setExpansionIdentifier(expProfileToAllValueSet);
				}
				if (searchDisplay.getValueSetView().getDefaultExpProfileSel().getValue()) {
					modifiedCqlValueSetList.add(cqlQualityDataSetDTO);
				}
			}
		}
		updateAllInLibraryXml(modifiedCqlValueSetList);
	}
	
	/**
	 * Update all in library xml.
	 *
	 * @param modifiedCqlQDMList the modified cql QDM list
	 */
	private void updateAllInLibraryXml(List<CQLQualityDataSetDTO> modifiedCqlQDMList) {
		String libraryId =  MatContext.get().getCurrentCQLLibraryId();
		MatContext.get().getLibraryService().updateCQLLibraryXMLForExpansionProfile(modifiedCqlQDMList, libraryId, expProfileToAllValueSet,
				new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				getAppliedValueSetList();
				if (!searchDisplay.getValueSetView().getDefaultExpProfileSel().getValue()) {
					searchDisplay.getCqlLeftNavBarPanelView().getSuccessMessageAlert().createAlert(MatContext.get()
							.getMessageDelegate().getDefaultExpansionIdRemovedMessage());
					
				} else {
					searchDisplay.getCqlLeftNavBarPanelView().getSuccessMessageAlert().createAlert(MatContext.get()
							.getMessageDelegate().getVsacProfileAppliedToQdmElements());
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Window.alert(MatContext.get().getMessageDelegate().getGenericErrorMessage());
				
			}
		});
	}
	
	/**
	 * Build View for General info when General Info AnchorList item is clicked.
	 */
	private void generalInfoEvent() {
		searchDisplay.getCqlLeftNavBarPanelView().setIsNavBarClick(true);
		searchDisplay.getCqlLeftNavBarPanelView().setIsDoubleClick(false);
		if (searchDisplay.getCqlLeftNavBarPanelView().getIsPageDirty()) {
			nextSection = CQLWorkSpaceConstants.CQL_GENERAL_MENU;
			searchDisplay.getCqlLeftNavBarPanelView().showUnsavedChangesWarning();

		} else {
			unsetActiveMenuItem(currentSection);
			searchDisplay.getCqlLeftNavBarPanelView().getGeneralInformation().setActive(true);
			currentSection = CQLWorkSpaceConstants.CQL_GENERAL_MENU;
		    searchDisplay.buildGeneralInformation();
		}

	}

	/**
	 * Applied QDM event.
	 */
	private void appliedQDMEvent() {
		// server
		searchDisplay.getCqlLeftNavBarPanelView().setIsNavBarClick(true);
		searchDisplay.getCqlLeftNavBarPanelView().setIsDoubleClick(false);
		if (searchDisplay.getCqlLeftNavBarPanelView().getIsPageDirty()) {
			nextSection = CQLWorkSpaceConstants.CQL_APPLIED_QDM;
			searchDisplay.getCqlLeftNavBarPanelView().showUnsavedChangesWarning();

		} else {
			unsetActiveMenuItem(currentSection);
			searchDisplay.getCqlLeftNavBarPanelView().getAppliedQDM().setActive(true);
			currentSection = CQLWorkSpaceConstants.CQL_APPLIED_QDM;
			searchDisplay.buildAppliedQDM();
			searchDisplay.getValueSetView().buildAppliedValueSetCellTable(searchDisplay.getCqlLeftNavBarPanelView().getAppliedQdmTableList(),
					MatContext.get().getLibraryLockService().checkForEditPermission());
			searchDisplay.getValueSetView()
					.setWidgetsReadOnly(MatContext.get().getLibraryLockService().checkForEditPermission());
			searchDisplay.getValueSetView().resetCQLValuesetearchPanel();
		}

	}

	/**
	 * Build View for Parameter when Parameter AnchorList item is clicked.
	 */
	private void parameterEvent() {
		unsetActiveMenuItem(currentSection);

		searchDisplay.getCqlLeftNavBarPanelView().getParameterLibrary().setActive(true);
		currentSection = CQLWorkSpaceConstants.CQL_PARAMETER_MENU;
		searchDisplay.buildParameterLibraryView();

		searchDisplay.getCQLParametersView().setWidgetReadOnly(
				MatContext.get().getLibraryLockService().checkForEditPermission());

		searchDisplay.getParameterButtonBar().getDeleteButton().setEnabled(false);
		searchDisplay.getParameterButtonBar().getDeleteButton().setTitle("Delete");
		curAceEditor = searchDisplay.getCQLParametersView().getParameterAceEditor();
	}

	/**
	 * Build View for Includes when Includes AnchorList item is clicked.
	 */
	private void includesEvent() {
		unsetActiveMenuItem(currentSection);
		searchDisplay.getCqlLeftNavBarPanelView().setIsNavBarClick(true);
		searchDisplay.getCqlLeftNavBarPanelView().setIsDoubleClick(false);

		searchDisplay.getCqlLeftNavBarPanelView().getIncludesLibrary().setActive(true);
		currentSection = CQLWorkSpaceConstants.CQL_INCLUDES_MENU;
		searchDisplay.getMainFlowPanel().clear();
		searchDisplay.getIncludeView().setIncludedList(searchDisplay.getCqlLeftNavBarPanelView()
				.getIncludedList(searchDisplay.getCqlLeftNavBarPanelView().getIncludeLibraryMap()));
		getAllIncludeLibraryList(searchDisplay.getIncludeView().getSearchTextBox().getText());
		searchDisplay.getIncludeView().getAliasNameTxtArea().setText("");
		searchDisplay.getCqlIncludeLibraryView().setWidgetReadOnly(
				MatContext.get().getLibraryLockService().checkForEditPermission());
	}
	
	/**
	 * Build View for Definition when Definition AnchorList item is clicked.
	 */
	private void definitionEvent() {
		unsetActiveMenuItem(currentSection);
		searchDisplay.getCqlLeftNavBarPanelView().setIsNavBarClick(true);
		searchDisplay.getCqlLeftNavBarPanelView().setIsDoubleClick(false);

		searchDisplay.getCqlLeftNavBarPanelView().getDefinitionLibrary().setActive(true);
		currentSection = CQLWorkSpaceConstants.CQL_DEFINE_MENU;
		searchDisplay.buildDefinitionLibraryView();
		
		searchDisplay.getCQLDefinitionsView().setWidgetReadOnly(
				MatContext.get().getLibraryLockService().checkForEditPermission());

		searchDisplay.getDefineButtonBar().getDeleteButton().setEnabled(false);
		searchDisplay.getDefineButtonBar().getDeleteButton().setTitle("Delete");
		curAceEditor = searchDisplay.getCQLDefinitionsView().getDefineAceEditor();
	}

	/**
	 * Build View for Function when Funtion AnchorList item is clicked.
	 */
	private void functionEvent() {
		searchDisplay.getCqlLeftNavBarPanelView().setIsNavBarClick(true);
		searchDisplay.getCqlLeftNavBarPanelView().setIsDoubleClick(false);
		unsetActiveMenuItem(currentSection);
		searchDisplay.getCqlLeftNavBarPanelView().getFunctionLibrary().setActive(true);
		currentSection = CQLWorkSpaceConstants.CQL_FUNCTION_MENU;
		searchDisplay.buildFunctionLibraryView();
		searchDisplay.getCQLFunctionsView().setWidgetReadOnly(
				MatContext.get().getLibraryLockService().checkForEditPermission());

		searchDisplay.getFunctionButtonBar().getDeleteButton().setEnabled(false);
		searchDisplay.getFunctionButtonBar().getDeleteButton().setTitle("Delete");
		curAceEditor = searchDisplay.getCQLFunctionsView().getFunctionBodyAceEditor();
	}

	/**
	 * Build View for View Cql when View Cql AnchorList item is clicked.
	 */
	private void viewCqlEvent() {
		searchDisplay.getCqlLeftNavBarPanelView().setIsNavBarClick(true);
		searchDisplay.getCqlLeftNavBarPanelView().setIsDoubleClick(false);
		if (searchDisplay.getCqlLeftNavBarPanelView().getIsPageDirty()) {
			nextSection = CQLWorkSpaceConstants.CQL_VIEW_MENU;
			searchDisplay.getCqlLeftNavBarPanelView().showUnsavedChangesWarning();
		} else {
			unsetActiveMenuItem(currentSection);
			searchDisplay.getCqlLeftNavBarPanelView().getViewCQL().setActive(true);
			currentSection = CQLWorkSpaceConstants.CQL_VIEW_MENU;
			searchDisplay.buildCQLFileView();
			buildCQLView();
		}

	}
	
	
	
	/**
	 * Method to build View for Anchor List item View CQL.
	 */
	private void buildCQLView() {
		searchDisplay.getCqlAceEditor().setText("");
		showSearchingBusy(true);
		MatContext.get().getCQLLibraryService().getLibraryCQLFileData(MatContext.get().getCurrentCQLLibraryId(),
				new AsyncCallback<SaveUpdateCQLResult>() {
					@Override
					public void onSuccess(SaveUpdateCQLResult result) {
						if (result.isSuccess()) {
							if ((result.getCqlString() != null) && !result.getCqlString().isEmpty()) {
								//validateViewCQLFile(result.getCqlString());
								// searchDisplay.getCqlAceEditor().setText(result.getCqlString());
								
								searchDisplay.getCqlAceEditor().clearAnnotations();
								searchDisplay.getCqlAceEditor().removeAllMarkers();
								searchDisplay.getCqlAceEditor().redisplay();
								searchDisplay.getCqlLeftNavBarPanelView().getSuccessMessageAlert().clear();
								searchDisplay.getCqlLeftNavBarPanelView().getWarningMessageAlert().clear();
								searchDisplay.getCqlLeftNavBarPanelView().getWarningConfirmationMessageAlert().clear();

								if (!result.getCqlErrors().isEmpty()) {
									searchDisplay.getCqlLeftNavBarPanelView().getWarningMessageAlert().createAlert(
											MatContext.get().getMessageDelegate().getVIEW_CQL_ERROR_MESSAGE());
									for (CQLErrors error : result.getCqlErrors()) {
										String errorMessage = new String();
										errorMessage = errorMessage.concat("Error in line : " + error.getErrorInLine() + " at Offset :"
												+ error.getErrorAtOffeset());
										int line = error.getErrorInLine();
										int column = error.getErrorAtOffeset();
										searchDisplay.getCqlAceEditor().addAnnotation(line - 1, column, error.getErrorMessage(),
												AceAnnotationType.WARNING);
									}
									searchDisplay.getCqlAceEditor().setText(result.getCqlString());
									searchDisplay.getCqlAceEditor().setAnnotations();
									searchDisplay.getCqlAceEditor().redisplay();
								} else {
									searchDisplay.getCqlLeftNavBarPanelView().getSuccessMessageAlert().setVisible(true);
									searchDisplay.getCqlLeftNavBarPanelView().getSuccessMessageAlert()
											.createAlert(MatContext.get().getMessageDelegate().getVIEW_CQL_NO_ERRORS_MESSAGE());
									searchDisplay.getCqlAceEditor().setText(result.getCqlString());
								}
							}

						}
						showSearchingBusy(false);
					}

					@Override
					public void onFailure(Throwable caught) {
						Window.alert(MatContext.get().getMessageDelegate().getGenericErrorMessage());
						showSearchingBusy(false);
					}
				});

	}

	
	private void unsetActiveMenuItem(String menuClickedBefore) {
		if (!searchDisplay.getCqlLeftNavBarPanelView().getIsPageDirty()) {
			searchDisplay.resetMessageDisplay();
			if (menuClickedBefore.equalsIgnoreCase(CQLWorkSpaceConstants.CQL_GENERAL_MENU)) {
				searchDisplay.getCqlLeftNavBarPanelView().getGeneralInformation().setActive(false);
			} else if (menuClickedBefore.equalsIgnoreCase(CQLWorkSpaceConstants.CQL_PARAMETER_MENU)) {
				searchDisplay.getCqlLeftNavBarPanelView().getParameterLibrary().setActive(false);
				searchDisplay.getCqlLeftNavBarPanelView().getParameterNameListBox().setSelectedIndex(-1);
				if (searchDisplay.getCqlLeftNavBarPanelView().getParamCollapse().getElement().getClassName()
						.equalsIgnoreCase("panel-collapse collapse in")) {
					searchDisplay.getCqlLeftNavBarPanelView().getParamCollapse().getElement().setClassName("panel-collapse collapse");
				}
			} else if (menuClickedBefore.equalsIgnoreCase(CQLWorkSpaceConstants.CQL_DEFINE_MENU)) {
				searchDisplay.getCqlLeftNavBarPanelView().getDefinitionLibrary().setActive(false);
				searchDisplay.getCqlLeftNavBarPanelView().getDefineNameListBox().setSelectedIndex(-1);
				if (searchDisplay.getCqlLeftNavBarPanelView().getDefineCollapse().getElement().getClassName()
						.equalsIgnoreCase("panel-collapse collapse in")) {
					searchDisplay.getCqlLeftNavBarPanelView().getDefineCollapse().getElement().setClassName("panel-collapse collapse");
				}
			} else if (menuClickedBefore.equalsIgnoreCase(CQLWorkSpaceConstants.CQL_FUNCTION_MENU)) {
				searchDisplay.getCqlLeftNavBarPanelView().getFunctionLibrary().setActive(false);
				searchDisplay.getCqlLeftNavBarPanelView().getFuncNameListBox().setSelectedIndex(-1);
				if (searchDisplay.getCqlLeftNavBarPanelView().getFunctionCollapse().getElement().getClassName()
						.equalsIgnoreCase("panel-collapse collapse in")) {
					searchDisplay.getCqlLeftNavBarPanelView().getFunctionCollapse().getElement().setClassName("panel-collapse collapse");
				}
			} else if (menuClickedBefore.equalsIgnoreCase(CQLWorkSpaceConstants.CQL_VIEW_MENU)) {
				searchDisplay.getCqlLeftNavBarPanelView().getViewCQL().setActive(false);
			} else if (menuClickedBefore.equalsIgnoreCase(CQLWorkSpaceConstants.CQL_APPLIED_QDM)) {
				searchDisplay.getCqlLeftNavBarPanelView().getAppliedQDM().setActive(false);
			} else if (menuClickedBefore.equalsIgnoreCase(CQLWorkSpaceConstants.CQL_INCLUDES_MENU)) {
				searchDisplay.getCqlLeftNavBarPanelView().getIncludesLibrary().setActive(false);
				searchDisplay.getCqlLeftNavBarPanelView().getIncludesNameListbox().setSelectedIndex(-1);
				if (searchDisplay.getCqlLeftNavBarPanelView().getIncludesCollapse().getElement().getClassName()
						.equalsIgnoreCase("panel-collapse collapse in")) {
					searchDisplay.getCqlLeftNavBarPanelView().getIncludesCollapse().getElement().setClassName("panel-collapse collapse");
				}
			}
		}
	}
	
	/**
	 * This method is called at beforeDisplay and get searchButton click on Include section
	 * and reterives CQL Versioned libraries eligible to be included into any parent cql library.
	 *
	 * @param searchText the search text
	 * @return the all include library list
	 */
	private void getAllIncludeLibraryList(final String searchText) {
		searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert().clearAlert();
		searchDisplay.getCqlLeftNavBarPanelView().getSuccessMessageAlert().clearAlert();
		searchDisplay.getCqlLeftNavBarPanelView().getWarningMessageAlert().clearAlert();
		showSearchingBusy(true);
		
		MatContext.get().getCQLLibraryService().searchForIncludes(searchText, new AsyncCallback<SaveCQLLibraryResult>() {

			@Override
			public void onFailure(Throwable caught) {
				searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert().createAlert(MatContext.get().getMessageDelegate().getGenericErrorMessage());
				showSearchingBusy(false);
			}

			@Override
			public void onSuccess(SaveCQLLibraryResult result) {
				showSearchingBusy(false);
				if(result != null && result.getCqlLibraryDataSetObjects().size() > 0){
					searchDisplay.getCqlLeftNavBarPanelView().setIncludeLibraryList(result.getCqlLibraryDataSetObjects());
					searchDisplay.buildIncludesView();
					searchDisplay.getIncludeView().buildIncludeLibraryCellTable(result,MatContext.get().getLibraryLockService().checkForEditPermission());
					
				} else {
					searchDisplay.buildIncludesView();
					searchDisplay.getIncludeView().buildIncludeLibraryCellTable(result,MatContext.get().getLibraryLockService().checkForEditPermission());
					if(!searchDisplay.getIncludeView().getSearchTextBox().getText().isEmpty())
						searchDisplay.getCqlLeftNavBarPanelView().getErrorMessageAlert().createAlert(MatContext.get().getMessageDelegate().getNoIncludes());
				}
				
				
				if(searchDisplay.getCqlLeftNavBarPanelView().getIncludesNameListbox().getItemCount() >= CQLWorkSpaceConstants.VALID_INCLUDE_COUNT){
					searchDisplay.getCqlLeftNavBarPanelView().getWarningMessageAlert().createAlert(MatContext.get().getMessageDelegate().getCqlLimitWarningMessage());
				} else{
					searchDisplay.getCqlLeftNavBarPanelView().getWarningMessageAlert().clearAlert();
				}
				
			}
		});
		
	}
	
	public void showSearchingBusy(final boolean busy) {
		if (busy) {
			Mat.showLoadingMessage();
		} else {
			Mat.hideLoadingMessage();
		}
		searchDisplay.getCqlLeftNavBarPanelView().getGeneralInformation().setEnabled(!busy);
		if(!currentSection.equalsIgnoreCase(CQLWorkSpaceConstants.CQL_INCLUDES_MENU))
			searchDisplay.getCqlLeftNavBarPanelView().getIncludesLibrary().setEnabled(!busy);
		searchDisplay.getCqlLeftNavBarPanelView().getAppliedQDM().setEnabled(!busy);
		searchDisplay.getCqlLeftNavBarPanelView().getParameterLibrary().setEnabled(!busy);
		searchDisplay.getCqlLeftNavBarPanelView().getDefinitionLibrary().setEnabled(!busy);
		searchDisplay.getCqlLeftNavBarPanelView().getFunctionLibrary().setEnabled(!busy);
		searchDisplay.getCqlLeftNavBarPanelView().getViewCQL().setEnabled(!busy);
		if(MatContext.get().getLibraryLockService().checkForEditPermission()) {
			searchDisplay.getCqlGeneralInformationView().setWidgetReadOnly(!busy);
			searchDisplay.getIncludeView().getSaveButton().setEnabled(!busy);
			searchDisplay.getIncludeView().getEraseButton().setEnabled(!busy);
			
			searchDisplay.getCQLDefinitionsView().getDefineButtonBar().getSaveButton().setEnabled(!busy);
			searchDisplay.getCQLDefinitionsView().getDefineButtonBar().getEraseButton().setEnabled(!busy);
			searchDisplay.getCQLDefinitionsView().getDefineButtonBar().getDeleteButton().setEnabled(!busy);
			searchDisplay.getCQLDefinitionsView().getDefineButtonBar().getInsertButton().setEnabled(!busy);
			searchDisplay.getCQLDefinitionsView().getDefineButtonBar().getInfoButton().setEnabled(!busy);
			
			searchDisplay.getCQLParametersView().getParameterButtonBar().getSaveButton().setEnabled(!busy);
			searchDisplay.getCQLParametersView().getParameterButtonBar().getEraseButton().setEnabled(!busy);
			searchDisplay.getCQLParametersView().getParameterButtonBar().getDeleteButton().setEnabled(!busy);
			searchDisplay.getCQLParametersView().getParameterButtonBar().getInfoButton().setEnabled(!busy);
			
			searchDisplay.getCQLFunctionsView().getFunctionButtonBar().getSaveButton().setEnabled(!busy);
			searchDisplay.getCQLFunctionsView().getFunctionButtonBar().getEraseButton().setEnabled(!busy);
			searchDisplay.getCQLFunctionsView().getFunctionButtonBar().getDeleteButton().setEnabled(!busy);
			searchDisplay.getCQLFunctionsView().getFunctionButtonBar().getInsertButton().setEnabled(!busy);
			searchDisplay.getCQLFunctionsView().getFunctionButtonBar().getInfoButton().setEnabled(!busy);
			searchDisplay.getCQLFunctionsView().getAddNewArgument().setEnabled(!busy);
			
			searchDisplay.getValueSetView().getApplyDefaultExpansionIdButton().setEnabled(!busy);
			searchDisplay.getValueSetView().getSaveButton().setEnabled(!busy);
			searchDisplay.getValueSetView().getCancelQDMButton().setEnabled(!busy);
			searchDisplay.getValueSetView().getUpdateFromVSACButton().setEnabled(!busy);
			searchDisplay.getValueSetView().getRetrieveFromVSACButton().setEnabled(!busy);
		}
		searchDisplay.getIncludeView().getSearchButton().setEnabled(!busy);
		
		
	}
	
	
	/**
	 * This method Clears alias view on Erase Button click when isPageDirty
	 * is not set.
	 */
	private void clearAlias() {
		searchDisplay.getCqlLeftNavBarPanelView().setCurrentSelectedIncLibraryObjId(null);
		searchDisplay.getCqlLeftNavBarPanelView().setIsPageDirty(false);
		if ((searchDisplay.getIncludeView().getAliasNameTxtArea() != null)) {
			searchDisplay.getIncludeView().getAliasNameTxtArea().setText("");
		}
		if((searchDisplay.getIncludeView().getViewCQLEditor().getText() != null)){
			searchDisplay.getIncludeView().getViewCQLEditor().setText("");
		}
		//Below lines are to clear Library search text box.
		if((searchDisplay.getIncludeView().getSearchTextBox().getText() != null)){
			searchDisplay.getIncludeView().getSearchTextBox().setText("");
		}
		searchDisplay.getIncludeView().getSelectedObjectList().clear();
		searchDisplay.getIncludeView().setSelectedObject(null);
		searchDisplay.getIncludeView().setIncludedList(searchDisplay.getCqlLeftNavBarPanelView().getIncludedList(searchDisplay.getCqlLeftNavBarPanelView().getIncludeLibraryMap()));
		unCheckAvailableLibraryCheckBox();
		
		// Below lines are to clear search suggestion textbox and listbox
		// selection after erase.
		searchDisplay.getCqlLeftNavBarPanelView().getSearchSuggestIncludeTextBox().setText("");
		if (searchDisplay.getCqlLeftNavBarPanelView().getIncludesNameListbox().getSelectedIndex() >= 0) {
			searchDisplay.getCqlLeftNavBarPanelView().getIncludesNameListbox()
					.setItemSelected(searchDisplay.getCqlLeftNavBarPanelView().getIncludesNameListbox().getSelectedIndex(), false);
		}

	}
	
	/**
	 * Un check available library check box.
	 */
	private void unCheckAvailableLibraryCheckBox() {
		List<CQLLibraryDataSetObject> availableLibraries = new ArrayList<CQLLibraryDataSetObject>();
		availableLibraries = searchDisplay.getCqlLeftNavBarPanelView().getIncludeLibraryList();
		for (int i = 0; i < availableLibraries.size(); i++) {
			availableLibraries.get(i).setSelected(false);
		}
		/*searchDisplay.getIncludeView().buildIncludeLibraryCellTable(availableLibraries, 
				MatContext.get().getLibraryLockService().checkForEditPermission());*/
		SaveCQLLibraryResult result = new SaveCQLLibraryResult();
		result.setCqlLibraryDataSetObjects(searchDisplay.getCqlLeftNavBarPanelView().getIncludeLibraryList());
		searchDisplay.getIncludeView().buildIncludeLibraryCellTable(
				result,MatContext.get().getLibraryLockService().checkForEditPermission());
	}
	
	 /** 
	  * Validate CQL artifact.
	 *
	 * @param result the result
	 * @param currentSect the current sect
	 * @return true, if successful
	 */
	private boolean validateCQLArtifact(SaveUpdateCQLResult result, String currentSect) {
		boolean isInvalid = false;
		if (!result.getCqlErrors().isEmpty()) {
			//final AceEditor editor = getAceEditorBasedOnCurrentSection(searchDisplay, currentSection);
			for (CQLErrors error : result.getCqlErrors()) {
				int startLine = error.getStartErrorInLine();
				int startColumn = error.getStartErrorAtOffset();
				curAceEditor.addAnnotation(startLine, startColumn, error.getErrorMessage(), AceAnnotationType.WARNING);
				if (!isInvalid) {
					isInvalid = true;
				}
			}
		}

		return isInvalid;
	}
	 
	
	/**
	 * Gets the ace editor based on current section.
	 *
	 * @param searchDisplay the search display
	 * @param currentSection the current section
	 * @return the ace editor based on current section
	 */
	/*private static AceEditor getAceEditorBasedOnCurrentSection(ViewDisplay searchDisplay, String currentSection) {
		AceEditor editor = null;
		switch (currentSection) {
		case CQLWorkSpaceConstants.CQL_DEFINE_MENU:
			editor = searchDisplay.getCQLDefinitionsView().getDefineAceEditor();
			break;
		case CQLWorkSpaceConstants.CQL_FUNCTION_MENU:
			editor = searchDisplay.getCQLFunctionsView().getFunctionBodyAceEditor();
			break;
		case CQLWorkSpaceConstants.CQL_PARAMETER_MENU:
			editor = searchDisplay.getCQLParametersView().getParameterAceEditor();
			break;
		default:
			 editor = searchDisplay.getDefineAceEditor(); 
			break;
		}
		return editor;
	}*/
	
	/**
	 * Method to trigger double Click on List Boxes based on section when user
	 * clicks Yes on Warning message (Dirty Check).
	 */
	private void clickEventOnListboxes() {

		searchDisplay.getCqlLeftNavBarPanelView().setIsDoubleClick(false);
		searchDisplay.getCqlLeftNavBarPanelView().setIsNavBarClick(false);
		switch (currentSection) {
		case (CQLWorkSpaceConstants.CQL_FUNCTION_MENU):
			searchDisplay.getCqlLeftNavBarPanelView().getFuncNameListBox().fireEvent(new DoubleClickEvent() {
			});
			break;
		case (CQLWorkSpaceConstants.CQL_PARAMETER_MENU):
			searchDisplay.getCqlLeftNavBarPanelView().getParameterNameListBox().fireEvent(new DoubleClickEvent() {
			});
			break;
		case (CQLWorkSpaceConstants.CQL_DEFINE_MENU):
			searchDisplay.getCqlLeftNavBarPanelView().getDefineNameListBox().fireEvent(new DoubleClickEvent() {
			});
			break;
		case (CQLWorkSpaceConstants.CQL_INCLUDES_MENU):
			searchDisplay.getCqlLeftNavBarPanelView().getIncludesNameListbox().fireEvent(new DoubleClickEvent() {
			});
			break;
		default:
			break;
		}

	}

	/**
	 * Method to Unset current Left Nav section and set next selected section
	 * when user clicks yes on warning message (Dirty Check).
	 */
	private void changeSectionSelection() {
		// Unset current selected section.
		switch (currentSection) {
		case (CQLWorkSpaceConstants.CQL_INCLUDES_MENU):
			unsetActiveMenuItem(currentSection);
		    searchDisplay.getCqlLeftNavBarPanelView().getIncludesLibrary().setActive(false);
			break;
		case (CQLWorkSpaceConstants.CQL_APPLIED_QDM):
			unsetActiveMenuItem(currentSection);
		    searchDisplay.getCqlLeftNavBarPanelView().getAppliedQDM().setActive(false);
			break;
		case (CQLWorkSpaceConstants.CQL_FUNCTION_MENU):
			unsetActiveMenuItem(currentSection);
		    searchDisplay.getCqlLeftNavBarPanelView().getFunctionLibrary().setActive(false);
			break;
		case (CQLWorkSpaceConstants.CQL_PARAMETER_MENU):
			unsetActiveMenuItem(currentSection);
		    searchDisplay.getCqlLeftNavBarPanelView().getParameterLibrary().setActive(false);
			break;
		case (CQLWorkSpaceConstants.CQL_DEFINE_MENU):
			unsetActiveMenuItem(currentSection);
		    searchDisplay.getCqlLeftNavBarPanelView().getDefinitionLibrary().setActive(false);
			break;
		case (CQLWorkSpaceConstants.CQL_GENERAL_MENU):
			unsetActiveMenuItem(currentSection);
		    searchDisplay.getCqlLeftNavBarPanelView().getGeneralInformation().setActive(false);
			break;
		case (CQLWorkSpaceConstants.CQL_VIEW_MENU):
			unsetActiveMenuItem(currentSection);
		    searchDisplay.getCqlLeftNavBarPanelView().getViewCQL().setActive(false);
			break;
		default:
			break;
		}
		// Set Next Selected Section.
		switch (nextSection) {
		case (CQLWorkSpaceConstants.CQL_INCLUDES_MENU):
			currentSection = nextSection;
			includesEvent();
			searchDisplay.getCqlLeftNavBarPanelView().getIncludesCollapse().getElement().setClassName("panel-collapse collapse in");
			break;
		case (CQLWorkSpaceConstants.CQL_FUNCTION_MENU):
			currentSection = nextSection;
			functionEvent();
			searchDisplay.getCqlLeftNavBarPanelView().getFunctionCollapse().getElement().setClassName("panel-collapse collapse in");
			break;
		case (CQLWorkSpaceConstants.CQL_PARAMETER_MENU):
			currentSection = nextSection;
			parameterEvent();
			searchDisplay.getCqlLeftNavBarPanelView().getParamCollapse().getElement().setClassName("panel-collapse collapse in");
			break;
		case (CQLWorkSpaceConstants.CQL_DEFINE_MENU):
			currentSection = nextSection;
			definitionEvent();
			searchDisplay.getCqlLeftNavBarPanelView().getDefineCollapse().getElement().setClassName("panel-collapse collapse in");
			break;
		case (CQLWorkSpaceConstants.CQL_GENERAL_MENU):
			currentSection = nextSection;
			generalInfoEvent();
			break;
		case (CQLWorkSpaceConstants.CQL_VIEW_MENU):
			currentSection = nextSection;
			viewCqlEvent();
			break;
		case (CQLWorkSpaceConstants.CQL_APPLIED_QDM):
			currentSection = nextSection;
			appliedQDMEvent();
			break;
		default:
			break;
		}
	}

	/**
	 * This method clears the view if isPageDirty flag is not set.
	 */
	private void clearViewIfDirtyNotSet() {
		switch (currentSection) {
		case (CQLWorkSpaceConstants.CQL_FUNCTION_MENU):
			clearFunction();
			break;
		case (CQLWorkSpaceConstants.CQL_PARAMETER_MENU):
			clearParameter();
			break;
		case (CQLWorkSpaceConstants.CQL_DEFINE_MENU):
			clearDefinition();
			break;
		default:
			break;
		}
	}

	/**
	 * Get Attributed for Selected Function Argument - QDM Data Type from db.
	 *
	 * @param functionArg
	 *            - CQLFunctionArgument.
	 * @return the attributes for data type
	 */
	private void getAttributesForDataType(final CQLFunctionArgument functionArg) {
		attributeService.getAllAttributesByDataType(functionArg.getQdmDataType(),
				new AsyncCallback<List<QDSAttributes>>() {

					@Override
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
						System.out.println("Error retrieving data type attributes. " + caught.getMessage());

					}

					@Override
					public void onSuccess(List<QDSAttributes> result) {
						searchDisplay.getCqlLeftNavBarPanelView().setAvailableQDSAttributeList(result);
						AddFunctionArgumentDialogBox.showArgumentDialogBox(functionArg, true, searchDisplay.getCQLFunctionsView(),MatContext.get().getLibraryLockService().checkForEditPermission());

					}

				});
	}
	
	/**
	 * Gets the definition list.
	 *
	 * @param definitionList
	 *            the definition list
	 * @return the definition list
	 */
	private List<String> getDefinitionList(List<CQLDefinition> definitionList) {

		List<String> defineList = new ArrayList<String>();

		for (int i = 0; i < definitionList.size(); i++) {
			defineList.add(definitionList.get(i).getDefinitionName());
		}
		return defineList;
	}
	
	/**
	 * Gets the paramater list.
	 *
	 * @param parameterList
	 *            the parameter list
	 * @return the paramater list
	 */
	private List<String> getParamaterList(List<CQLParameter> parameterList) {

		List<String> paramList = new ArrayList<String>();

		for (int i = 0; i < parameterList.size(); i++) {
			paramList.add(parameterList.get(i).getParameterName());
		}
		return paramList;
	}

	/**
	 * Gets the function list.
	 *
	 * @param functionList
	 *            the function list
	 * @return the function list
	 */
	private List<String> getFunctionList(List<CQLFunctions> functionList) {

		List<String> funcList = new ArrayList<String>();

		for (int i = 0; i < functionList.size(); i++) {
			funcList.add(functionList.get(i).getFunctionName());
		}
		return funcList;
	}
	
	/**
	 * Gets the includes list.
	 *
	 * @param includesList the includes list
	 * @return the includes list
	 */
	private List<String> getIncludesList(List<CQLIncludeLibrary> includesList) {

		List<String> incLibList = new ArrayList<String>();

		for (int i = 0; i < includesList.size(); i++) {
			incLibList.add(includesList.get(i).getAliasName());
		}
		return incLibList;
	}
	/**
	 * Display empty.
	 */
	private void displayEmpty() {
		panel.clear();
		panel.add(emptyWidget);
	}

	@Override
	public Widget getWidget() {
		panel.setStyleName("contentPanel");
		return panel;
	}
	
	/**
	 * returns the searchDisplay.
	 * 
	 * @return ViewDisplay.
	 */
	public ViewDisplay getSearchDisplay() {
		return searchDisplay;
	}

}
