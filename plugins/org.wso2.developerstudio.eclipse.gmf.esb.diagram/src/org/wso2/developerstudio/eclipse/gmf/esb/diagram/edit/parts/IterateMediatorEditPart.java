/*
 * Copyright (c) 2014-2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wso2.developerstudio.eclipse.gmf.esb.diagram.edit.parts;

import static org.wso2.developerstudio.eclipse.gmf.esb.diagram.edit.parts.EditPartConstants.DEFAULT_PROPERTY_VALUE_TEXT;
import static org.wso2.developerstudio.eclipse.gmf.esb.diagram.edit.parts.EditPartConstants.ITERATE_MEDIATOR_ICON_PATH;

import org.apache.axiom.om.OMElement;
import org.apache.commons.lang.StringUtils;
import org.apache.synapse.SynapseException;
import org.apache.synapse.config.xml.IterateMediatorSerializer;
import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.StackLayout;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.LayoutEditPolicy;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IBorderItemEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.BorderItemSelectionEditPolicy;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.CreationEditPolicy;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.DragDropEditPolicy;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.EditPolicyRoles;
import org.eclipse.gmf.runtime.diagram.ui.figures.BorderItemLocator;
import org.eclipse.gmf.runtime.draw2d.ui.figures.ConstrainedToolbarLayout;
import org.eclipse.gmf.runtime.draw2d.ui.figures.WrappingLabel;
import org.eclipse.gmf.runtime.gef.ui.figures.DefaultSizeNodeFigure;
import org.eclipse.gmf.runtime.gef.ui.figures.NodeFigure;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.papyrus.infra.gmfdiag.css.CSSNodeImpl;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.jaxen.JaxenException;
import org.wso2.developerstudio.eclipse.gmf.esb.EsbNode;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.Activator;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.custom.EsbGroupingShape;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.custom.FixedBorderItemLocator;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.custom.ShowPropertyViewEditPolicy;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.custom.SingleCompartmentComplexFiguredAbstractMediator;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.custom.editpolicy.FeedbackIndicateDragDropEditPolicy;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.custom.utils.CustomToolTip;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.edit.policies.IterateMediatorCanonicalEditPolicy;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.edit.policies.IterateMediatorItemSemanticEditPolicy;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.part.EsbVisualIDRegistry;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.validator.GraphicalValidatorUtil;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.validator.MediatorValidationUtil;
import org.wso2.developerstudio.eclipse.gmf.esb.impl.IterateMediatorImpl;
import org.wso2.developerstudio.eclipse.gmf.esb.internal.persistence.IterateMediatorTransformer;
import org.wso2.developerstudio.eclipse.gmf.esb.persistence.TransformationInfo;
import org.wso2.developerstudio.eclipse.gmf.esb.persistence.TransformerException;
import org.wso2.developerstudio.eclipse.logging.core.IDeveloperStudioLog;
import org.wso2.developerstudio.eclipse.logging.core.Logger;

/**
 * @generated NOT
 */
public class IterateMediatorEditPart extends SingleCompartmentComplexFiguredAbstractMediator {

    private static IDeveloperStudioLog log = Logger.getLog(Activator.PLUGIN_ID);
    public IFigure targetOutputConnector;

    /**
     * @generated
     */
    public static final int VISUAL_ID = 3519;

    /**
     * @generated
     */
    protected IFigure contentPane;

    /**
     * @generated
     */
    public IterateMediatorEditPart(View view) {
        super(view);
    }

    /**
     * @generated NOT
     */
    protected void createDefaultEditPolicies() {
        installEditPolicy(EditPolicyRoles.CREATION_ROLE, new CreationEditPolicy());
        super.createDefaultEditPolicies();
        installEditPolicy(EditPolicyRoles.SEMANTIC_ROLE, new IterateMediatorItemSemanticEditPolicy());
        installEditPolicy(EditPolicyRoles.DRAG_DROP_ROLE, new DragDropEditPolicy());
        installEditPolicy(EditPolicyRoles.DRAG_DROP_ROLE, new FeedbackIndicateDragDropEditPolicy());
        installEditPolicy(EditPolicyRoles.CANONICAL_ROLE, new IterateMediatorCanonicalEditPolicy());
        installEditPolicy(EditPolicy.LAYOUT_ROLE, createLayoutEditPolicy());
        // For handle Double click Event.
        installEditPolicy(EditPolicyRoles.OPEN_ROLE, new ShowPropertyViewEditPolicy());
        // XXX need an SCR to runtime to have another abstract superclass that would let children add reasonable
        // editpolicies
        // removeEditPolicy(org.eclipse.gmf.runtime.diagram.ui.editpolicies.EditPolicyRoles.CONNECTION_HANDLES_ROLE);
    }

    /**
     * @generated
     */
    protected LayoutEditPolicy createLayoutEditPolicy() {
        org.eclipse.gmf.runtime.diagram.ui.editpolicies.LayoutEditPolicy lep = new org.eclipse.gmf.runtime.diagram.ui.editpolicies.LayoutEditPolicy() {

            protected EditPolicy createChildEditPolicy(EditPart child) {
                View childView = (View) child.getModel();
                switch (EsbVisualIDRegistry.getVisualID(childView)) {
                case IterateMediatorInputConnectorEditPart.VISUAL_ID:
                case IterateMediatorOutputConnectorEditPart.VISUAL_ID:
                    return new BorderItemSelectionEditPolicy();
                }
                EditPolicy result = child.getEditPolicy(EditPolicy.PRIMARY_DRAG_ROLE);
                if (result == null) {
                    result = new NonResizableEditPolicy();
                }
                return result;
            }

            protected Command getMoveChildrenCommand(Request request) {
                return null;
            }

            protected Command getCreateCommand(CreateRequest request) {
                return null;
            }
        };
        return lep;
    }

    /**
     * @generated NOT
     */
    protected IFigure createNodeShape() {
        return primaryShape = new IterateMediatorFigure(new Color(null, 239, 187, 63), new Color(null, 255, 255, 255)) {
            public void setBounds(org.eclipse.draw2d.geometry.Rectangle rect) {
                super.setBounds(rect);
                if ((!connected) && (this.getBounds().getLocation().x != 0 && this.getBounds().getLocation().y != 0)) {
                    connectToMostSuitableElement();
                    reAllocate(rect);
                    connected = true;
                }
            };
        };
    }

    /**
     * @generated
     */
    public IterateMediatorFigure getPrimaryShape() {
        return (IterateMediatorFigure) primaryShape;
    }

    /**
     * @generated NOT
     */
    protected boolean addFixedChild(EditPart childEditPart) {
        if (childEditPart instanceof IterateMediatorIterateIDEditPart) {
            ((IterateMediatorIterateIDEditPart) childEditPart)
                    .setLabel(getPrimaryShape().getFigureIterateMediatorPropertyValue());
            return true;
        }
        if (childEditPart instanceof IterateMediatorInputConnectorEditPart) {
            IFigure borderItemFigure = ((IterateMediatorInputConnectorEditPart) childEditPart).getFigure();
            BorderItemLocator locator = new FixedBorderItemLocator(getMainFigure(), borderItemFigure,
                    PositionConstants.WEST, 0.5);
            getBorderedFigure().getBorderItemContainer().add(borderItemFigure, locator);
            return true;
        }
        if (childEditPart instanceof IterateMediatorOutputConnectorEditPart) {
            IFigure borderItemFigure = ((IterateMediatorOutputConnectorEditPart) childEditPart).getFigure();
            BorderItemLocator locator = new FixedBorderItemLocator(getMainFigure(), borderItemFigure,
                    PositionConstants.EAST, 0.5);
            getBorderedFigure().getBorderItemContainer().add(borderItemFigure, locator);
            return true;
        }
        if (childEditPart instanceof IterateMediatorTargetOutputConnectorEditPart) {
            targetOutputConnector = ((IterateMediatorTargetOutputConnectorEditPart) childEditPart).getFigure();
            targetOutputConnector.setEnabled(false);
        }
        return false;
    }

    /**
     * @generated
     */
    protected boolean removeFixedChild(EditPart childEditPart) {
        if (childEditPart instanceof IterateMediatorIterateIDEditPart) {
            return true;
        }
        if (childEditPart instanceof IterateMediatorInputConnectorEditPart) {
            getBorderedFigure().getBorderItemContainer()
                    .remove(((IterateMediatorInputConnectorEditPart) childEditPart).getFigure());
            return true;
        }
        if (childEditPart instanceof IterateMediatorOutputConnectorEditPart) {
            getBorderedFigure().getBorderItemContainer()
                    .remove(((IterateMediatorOutputConnectorEditPart) childEditPart).getFigure());
            return true;
        }
        return false;
    }

    /**
     * @generated
     */
    protected void addChildVisual(EditPart childEditPart, int index) {
        if (addFixedChild(childEditPart)) {
            return;
        }
        super.addChildVisual(childEditPart, -1);
    }

    /**
     * @generated
     */
    protected void removeChildVisual(EditPart childEditPart) {
        if (removeFixedChild(childEditPart)) {
            return;
        }
        super.removeChildVisual(childEditPart);
    }

    /**
     * @generated
     */
    protected IFigure getContentPaneFor(IGraphicalEditPart editPart) {
        if (editPart instanceof IBorderItemEditPart) {
            return getBorderedFigure().getBorderItemContainer();
        }
        return getContentPane();
    }

    /**
     * @generated
     */
    protected NodeFigure createNodePlate() {
        DefaultSizeNodeFigure result = new DefaultSizeNodeFigure(40, 40);
        return result;
    }

    /**
     * Creates figure for this edit part.
     * 
     * Body of this method does not depend on settings in generation model
     * so you may safely remove <i>generated</i> tag and modify it.
     * 
     * @generated NOT
     */
    protected NodeFigure createMainFigure() {
        NodeFigure figure = createNodePlate();
        figure.setLayoutManager(new StackLayout());
        IFigure shape = createNodeShape();
        figure.add(shape);
        contentPane = setupContentPane(shape);
        addLayoutListner(figure);
        return figure;
    }

    /**
     * Default implementation treats passed figure as content pane.
     * Respects layout one may have set for generated figure.
     * 
     * @param nodeShape instance of generated figure class
     * @generated
     */
    protected IFigure setupContentPane(IFigure nodeShape) {
        if (nodeShape.getLayoutManager() == null) {
            ConstrainedToolbarLayout layout = new ConstrainedToolbarLayout();
            layout.setSpacing(5);
            nodeShape.setLayoutManager(layout);
        }
        return nodeShape; // use nodeShape itself as contentPane
    }

    /**
     * @generated
     */
    public IFigure getContentPane() {
        if (contentPane != null) {
            return contentPane;
        }
        return super.getContentPane();
    }

    /**
     * @generated
     */
    protected void setForegroundColor(Color color) {
        if (primaryShape != null) {
            primaryShape.setForegroundColor(color);
        }
    }

    /**
     * @generated
     */
    protected void setBackgroundColor(Color color) {
        if (primaryShape != null) {
            primaryShape.setBackgroundColor(color);
        }
    }

    /**
     * @generated
     */
    protected void setLineWidth(int width) {
        if (primaryShape instanceof Shape) {
            ((Shape) primaryShape).setLineWidth(width);
        }
    }

    /**
     * @generated
     */
    protected void setLineType(int style) {
        if (primaryShape instanceof Shape) {
            ((Shape) primaryShape).setLineStyle(style);
        }
    }

    /**
     * @generated
     */
    public EditPart getPrimaryChildEditPart() {
        return getChildBySemanticHint(EsbVisualIDRegistry.getType(IterateMediatorIterateIDEditPart.VISUAL_ID));
    }

    /**
     * @generated
     */
    public class IterateMediatorFigure extends EsbGroupingShape {

        /**
         * @generated
         */
        private WrappingLabel fFigureIterateMediatorPropertyValue;

        /**
         * @generated NOT
         */
        public IterateMediatorFigure(Color bgColor, Color boderColor) {
            super(bgColor, boderColor);
            ToolbarLayout layoutThis = new ToolbarLayout();
            layoutThis.setStretchMinorAxis(true);
            layoutThis.setMinorAlignment(ToolbarLayout.ALIGN_CENTER);

            layoutThis.setSpacing(0);
            layoutThis.setVertical(false);

            this.setLayoutManager(layoutThis);
            this.setPreferredSize(new Dimension(getMapMode().DPtoLP(170), getMapMode().DPtoLP(100)));
            this.setOutline(false);
            this.setBackgroundColor(THIS_BACK);
            createContents();
        }

        public void add(IFigure figure, Object constraint, int index) {
            if (figure instanceof DefaultSizeNodeFigure) {
                GridData layoutData = new GridData();
                layoutData.grabExcessHorizontalSpace = true;
                layoutData.grabExcessVerticalSpace = true;
                layoutData.horizontalAlignment = GridData.FILL;
                layoutData.verticalAlignment = GridData.FILL;
                super.add(figure, layoutData, index);
            } else if (figure instanceof RoundedRectangle) {
                GridData layoutData = new GridData();
                layoutData.grabExcessHorizontalSpace = true;
                layoutData.grabExcessVerticalSpace = true;
                layoutData.horizontalAlignment = GridData.FILL;
                layoutData.verticalAlignment = GridData.FILL;
                super.add(figure, layoutData, index);
            }

            else {
                super.add(figure, constraint, index);
            }
        }

        /**
         * @generated NOT
         */
        private void createContents() {

            fFigureIterateMediatorPropertyValue = new WrappingLabel();
            fFigureIterateMediatorPropertyValue.setText(DEFAULT_PROPERTY_VALUE_TEXT);
            fFigureIterateMediatorPropertyValue.setAlignment(SWT.CENTER);

        }

        /**
         * @generated
         */
        public WrappingLabel getFigureIterateMediatorPropertyValue() {
            return fFigureIterateMediatorPropertyValue;
        }

        public String getIconPath() {
            return ITERATE_MEDIATOR_ICON_PATH;
        }

        public String getNodeName() {
            return Messages.IterateMediatorEditPart_NodeName;
        }

        public IFigure getToolTip() {
            if (StringUtils.isEmpty(toolTipMessage)) {
                toolTipMessage = Messages.IterateMediatorEditPart_ToolTipMessage;
            }
            return new CustomToolTip().getCustomToolTipShape(toolTipMessage);
        }

    }

    @Override
    public void notifyChanged(Notification notification) {
        // this.getModel() will get EMF datamodel of the iterate mediator datamodel
        if (notification.getEventType() == Notification.SET && this.getModel() instanceof CSSNodeImpl) {
            // The following part will check for validation issues with the current data in
            // the model
            CSSNodeImpl model = (CSSNodeImpl) this.getModel();
            if (model.getElement() instanceof IterateMediatorImpl) {
                IterateMediatorImpl iterateMediatorDataModel = (IterateMediatorImpl) model.getElement();
                boolean hasError = false;
                try {
                    if (iterateMediatorDataModel.getIterateExpression().getPropertyValue().equals("")) {
                        hasError = true;
                    }
                    if (!hasError && iterateMediatorDataModel.isPreservePayload()) {
                        if (iterateMediatorDataModel.getAttachPath().getPropertyValue().equals("")) {
                            hasError = true;
                        }
                    }

                    if (hasError) {
                        GraphicalValidatorUtil.addValidationMark(this);
                    } else {
                        GraphicalValidatorUtil.removeValidationMark(this);
                    }
                } catch (Exception e) {
                    // Skip error since it's a validation related minor issue
                    log.error("Graphical validation error occured", e);
                }
            }
        }
        super.notifyChanged(notification);
    }

    /**
     * @generated NOT
     */
    static final Color THIS_BACK = new Color(null, 255, 255, 255);

}
