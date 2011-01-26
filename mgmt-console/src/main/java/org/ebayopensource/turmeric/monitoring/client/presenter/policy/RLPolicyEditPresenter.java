/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.monitoring.client.presenter.policy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ebayopensource.turmeric.monitoring.client.ConsoleUtil;
import org.ebayopensource.turmeric.monitoring.client.SupportedService;
import org.ebayopensource.turmeric.monitoring.client.model.ConsoleService;
import org.ebayopensource.turmeric.monitoring.client.model.HistoryToken;
import org.ebayopensource.turmeric.monitoring.client.model.policy.Condition;
import org.ebayopensource.turmeric.monitoring.client.model.policy.ConditionImpl;
import org.ebayopensource.turmeric.monitoring.client.model.policy.ExpressionImpl;
import org.ebayopensource.turmeric.monitoring.client.model.policy.Operation;
import org.ebayopensource.turmeric.monitoring.client.model.policy.PolicyQueryService.GetResourcesResponse;
import org.ebayopensource.turmeric.monitoring.client.model.policy.PolicyQueryService.ResourceLevel;
import org.ebayopensource.turmeric.monitoring.client.model.policy.PolicyQueryService.RuleEffectType;
import org.ebayopensource.turmeric.monitoring.client.model.policy.PolicyQueryService.UpdateMode;
import org.ebayopensource.turmeric.monitoring.client.model.policy.PolicyQueryService.UpdatePolicyResponse;
import org.ebayopensource.turmeric.monitoring.client.model.policy.GenericPolicy;
import org.ebayopensource.turmeric.monitoring.client.model.policy.PrimitiveValueImpl;
import org.ebayopensource.turmeric.monitoring.client.model.policy.Resource;
import org.ebayopensource.turmeric.monitoring.client.model.policy.ResourceKey;
import org.ebayopensource.turmeric.monitoring.client.model.policy.RuleAttribute;
import org.ebayopensource.turmeric.monitoring.client.model.policy.RuleAttributeImpl;
import org.ebayopensource.turmeric.monitoring.client.model.policy.RuleImpl;
import org.ebayopensource.turmeric.monitoring.client.model.policy.SupportedPrimitive;
import org.ebayopensource.turmeric.monitoring.client.presenter.policy.RLPolicyCreatePresenter.RLPolicyCreateDisplay;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;

public class RLPolicyEditPresenter extends PolicyEditPresenter {

	private List<Resource> resources = null;

	public RLPolicyEditPresenter(HandlerManager eventBus,
			PolicyEditDisplay view,
			Map<SupportedService, ConsoleService> serviceMap) {
		super(eventBus, view, serviceMap);
		view.setConditionBuilderVisible(true);
		bind();
	}

	public final static String PRESENTER_ID = "RLPolicyEdit";

	public interface RLPolicyEditDisplay extends RLPolicyCreateDisplay {

	}

	@Override
	public String getId() {
		return PRESENTER_ID;
	}

	@Override
	public List<String> getResourceLevels() {
		List<String> rsLevels = new ArrayList<String>();
		rsLevels.add(ResourceLevel.OPERATION.name());
		rsLevels.add(ResourceLevel.RESOURCE.name());

		return rsLevels;
	}

	@Override
	public void go(HasWidgets container, final HistoryToken token) {
		super.go(container, token);
		fetchServices();
		fetchConditions();

	}



	@Override
	protected void bindSaveButton() {
		{
			// fired on saved policy
			this.view.getSaveButton().addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					GWT.log("EDITION MODE:");
					String ruleName = view.getPolicyName().getValue();

					RuleEffectType ruleEffectType = RuleEffectType.valueOf(view.getExtraFieldValue(6));
					Integer priority = view.getExtraFieldValue(5)!=null ? Integer.valueOf(view.getExtraFieldValue(5)):null;
					Long rolloverPeriod = Long.valueOf(view.getExtraFieldValue(4));
					Long effectDuration = (view.getExtraFieldValue(3)==null? 0L :Long.valueOf(view.getExtraFieldValue(3)));
					Long conditionDuration = 0L;
					String value = view.getExtraFieldValue(7);
					
					RuleImpl rule = null;
					
					if(value!= null && !value.isEmpty()){
						PrimitiveValueImpl primitiveValueImpl = null;
						ExpressionImpl exp = null;
					
						primitiveValueImpl = new PrimitiveValueImpl();
						primitiveValueImpl.setValue(value);
						primitiveValueImpl.setType(SupportedPrimitive.STRING);
						exp = new ExpressionImpl();
						exp.setPrimitiveValue(primitiveValueImpl);
						Condition condition = new ConditionImpl(exp);
						List<RuleAttribute> attributeList = new ArrayList<RuleAttribute>();
						
						RuleAttributeImpl raMails = new RuleAttributeImpl(view.getExtraFieldValue(1));
						attributeList.add(raMails);
						RuleAttributeImpl raActive = new RuleAttributeImpl(RuleAttribute.NotifyActiveValue.valueOf(view.getExtraFieldValue(2).toUpperCase()));
						attributeList.add(raActive);
				
						rule = new RuleImpl(ruleName, null,ruleEffectType,  priority, rolloverPeriod, effectDuration, conditionDuration, condition, attributeList);
						rules.add(rule);
					}
					
				final GenericPolicy p = getPolicy(
							view.getPolicyName().getValue(),
							originalPolicyType,
							view.getPolicyDesc().getValue(),
							resourceAssignments, subjectAssignments,
							view.getPolicyEnabled(),
							Long.valueOf(originalPolicyId), rules);

					GWT.log("Updating policy: " + p.getId() + "-" + p.getName());

					service.updatePolicy(UpdateMode.REPLACE, p,
							new AsyncCallback<UpdatePolicyResponse>() {

								public void onFailure(Throwable err) {
									view.getResourceContentView()
											.error(ConsoleUtil.messages.serverError(err
													.getLocalizedMessage()));
								}

								public void onSuccess(
										UpdatePolicyResponse response) {
								    GWT.log("Updated policy");
								    RLPolicyEditPresenter.this.view.clear();
								    clearLists();
								    HistoryToken token = makeToken(
								                                   PolicyController.PRESENTER_ID,
								                                   PolicySummaryPresenter.PRESENTER_ID,
								                                   null);
								  
								    //Prefill the summary search with the policy we just modified
								    token.addValue(HistoryToken.SRCH_POLICY_TYPE, originalPolicyType);
								    token.addValue(HistoryToken.SRCH_POLICY_NAME, p.getName());
								    History.newItem(token.toString(), true);
								}
							});

				}
			});
		}
	}

	private void fetchServices() {
		List<ResourceKey> rsKeys = new ArrayList<ResourceKey>();
		ResourceKey rsKey = new ResourceKey();
		rsKey.setType("SERVICE");
		rsKeys.add(rsKey);

		service.getResources(rsKeys, new AsyncCallback<GetResourcesResponse>() {

			@Override
			public void onFailure(Throwable caught) {
				view.error(ConsoleUtil.messages.serverError(caught
						.getLocalizedMessage()));
			}

			@Override
			public void onSuccess(GetResourcesResponse result) {
				resources = new ArrayList<Resource>(result.getResources());
				List<String> rsNames = new ArrayList<String>();

				for (Resource resource : resources) {
					rsNames.add(resource.getResourceName());
				}
				view.setRsNames(rsNames);

			}
		});
	}

	@Override
	public void bind() {
		super.bind();
		
		this.view.getAddConditionButton().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				setConditionExtraField();

			}
		});

		// retrieve resource names based on selected type
		this.view.getRsListBox().addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				String rsName = view.getRsNameSelected();
				List<String> opNames = new ArrayList<String>();

				for (Resource resource : resources) {
					if (rsName.equals(resource.getResourceName())) {
						List<Operation> opList = resource.getOpList();
						if (opList != null && !opList.isEmpty()) {
							for (Operation operation : opList) {
								opNames.add(operation.getOperationName());
							}
						}
						break;
					}
				}

				view.setOpNames(opNames);
			}

		});

	}

	protected void setConditionExtraField() {
		StringBuilder conditionString = new StringBuilder(
				view.getRsNameSelected());
		conditionString.append(":");
		conditionString.append(view.getOpNameSelected());
		conditionString.append(".");
		conditionString.append(view.getConditionSelected());
		conditionString.append(view.getAritmSignSelected());
		conditionString.append(view.getQuantityBox() + " ");
		conditionString.append(view.getLogicOpSelected() == null ? "" : view
				.getLogicOpSelected() + " ");

		if (view.validAllConditionFields()) {
			// TODO Need to be improved at dynamic extra fields implementation
			view.setExtraFieldValue(7, conditionString.toString(), true);

		}
	}

	private void fetchConditions() {
		List<String> conditions = new ArrayList<String>();

		// TODO fetch this conditions from server or any dynamically way
		conditions.add("count");
		conditions.add("ext");
		conditions.add("hits");

		view.setConditionNames(conditions);
	}

	

}
