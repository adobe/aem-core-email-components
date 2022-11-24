/*******************************************************************************
 * Copyright 2018 Adobe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
(function($, ns, channel, window) {
    "use strict";

    var NS = ".cmp-segmenteditor";
    var NN_PREFIX = "item_";
    var PN_PANEL_TITLE = "cq:panelTitle";
    var PN_CONDITION = "condition";
    var PN_CUSTOM_SEGMENT_CONDITION = "customSegmentCondition";
    var PN_RESOURCE_TYPE = "sling:resourceType";
    var PN_COPY_FROM = "./@CopyFrom";

    var selectors = {
        self: "[data-cmp-is='segmentEditor']",
        items: {
            self: "coral-multifield-item",
            select: "coral-select.cmp-segmenteditor__item-condition",
            hiddenInputTitle: ".cmp-segmenteditor__item-title"
        },
        order: "[data-cmp-hook-segmenteditor='order']",
        delete: "[data-cmp-hook-segmenteditor='delete']",
        copy: "[data-cmp-hook-segmenteditor='copy']",
        add: "[data-cmp-hook-segmenteditor='add']",
        insertComponentDialog: {
            self: "coral-dialog.InsertComponentDialog",
            selectList: ".InsertComponentDialog-list"
        },
        item: {
            icon: "[data-cmp-hook-segmenteditor='itemIcon']",
            clone: "[data-cmp-hook-segmenteditor='itemClone']",
            title: "[data-cmp-hook-segmenteditor='itemTitle']",
            condition: "[data-cmp-hook-segmenteditor='itemCondition']",
            custom: ".custom-segment",
            hiddenItemResourceType: "[data-cmp-hook-segmenteditor='itemResourceType']",
            hiddenItemTemplatePath: "[data-cmp-hook-segmenteditor='itemTemplatePath']"
        }
    };

    /**
     * @typedef {Object} ChildrenEditorConfig Represents a Children Editor configuration object
     * @property {HTMLElement} el The HTMLElement representing this Children Editor
     */

    /**
     * Segment Editor
     *
     * @class SegmentEditor
     * @classdesc A Segment Editor is a dialog component based on a multifield that allows editing (adding, removing, renaming, re-ordering)
     * the child items of panel container components.
     * @param {SegmentEditorConfig} config The Segment Editor configuration object
     */
    var SegmentEditor = function(config) {
        this._config = config;
        this._elements = {};
        this._path = "";
        this._orderedChildren = [];
        this._deletedChildren = [];
        this._copiedChildren = [];
        this._init();

        var that = this;
        var registry = $(window).adaptTo("foundation-registry");
        registry.register("foundation.adapters", {
            type: "cmp-segmenteditor",
            selector: selectors.self,
            adapter: function() {
                return {
                    items: function() {
                        var items = [];
                        that._elements.self.items.getAll().forEach(function(item) {
                            var component = item.querySelector(selectors.item.icon + " [title]").getAttribute("title");
                            var title = item.querySelector(selectors.item.title);
                            var name = (title && title.name) ? title.name.match(".?/?(.+)/.*")[1] : "";
                            var description = Granite.I18n.get(component) + ((title && title.value) ? ": " + Granite.I18n.get(title.value) : "");
                            items.push({
                                name: name,
                                description: description
                            });
                        });
                        return items;
                    }
                };
            }
        });

        registry.register("foundation.adapters", {
            type: "foundation-toggleable",
            selector: selectors.item.custom,
            adapter: function(el) {
                var section = $(el);
                var customSegmentName = section.find(selectors.item.title).adaptTo("foundation-field");
                var customSegmentCondition = section.find(selectors.item.condition).adaptTo("foundation-field");

                return {
                    isOpen: function() {
                        return section.classList.contains("custom-segment-active");
                    },
                    show: function() {
                        section.addClass("custom-segment-active");
                        customSegmentName.setRequired(true);
                        customSegmentName.setDisabled(false);
                        customSegmentCondition.setRequired(true);
                        customSegmentCondition.setDisabled(false);
                        section.trigger("foundation-toggleable-show");
                    },
                    hide: function() {
                        customSegmentName.setRequired(false);
                        customSegmentName.setDisabled(true);
                        customSegmentCondition.setRequired(false);
                        customSegmentCondition.setDisabled(true);
                        section.removeClass("custom-segment-active");
                        section.trigger("foundation-toggleable-hide");
                    }
                };
            }
        });

    };

    SegmentEditor.prototype = (function() {

        return {

            constructor: SegmentEditor,

            /**
             * Persists item updates to an endpoint, returns a Promise for handling
             *
             * @returns {Promise} The promise for completion handling
             */
            update: function() {
                this._processChildren();
                return $.Deferred().resolve();
            },

            /**
             * Initializes the Segment Editor
             *
             * @private
             */
            _init: function() {
                this._elements.self = this._config.el;
                this._elements.add = this._elements.self.querySelectorAll(selectors.add)[0];
                this._elements.order = this._elements.self.querySelectorAll(selectors.order)[0];
                this._elements.delete = this._elements.self.querySelectorAll(selectors.delete)[0];
                this._elements.copy = this._elements.self.querySelectorAll(selectors.copy)[0];
                this._elements.items = this._elements.self.querySelectorAll(selectors.items.self);
                this._path = this._elements.self.dataset["containerPath"];

                // store a reference to the Segment Editor object
                $(this._elements.self).data("segmentEditor", this);

                this._bindEvents();
            },

            /**
             * Renders a component icon
             *
             * @private
             * @param {Granite.author.Component} component The component to render the icon for
             * @returns {HTMLElement} The rendered icon
             */
            _renderIcon: function(component) {
                var iconHTML;
                var iconName = component.componentConfig.iconName;
                var iconPath = component.componentConfig.iconPath;
                var abbreviation = component.componentConfig.abbreviation;

                if (iconName) {
                    iconHTML = new Coral.Icon().set({
                        icon: iconName
                    });
                } else if (iconPath) {
                    iconHTML = document.createElement("img");
                    iconHTML.src = iconPath;
                } else {
                    iconHTML = new Coral.Tag().set({
                        color: "grey",
                        size: "M",
                        label: {
                            textContent: abbreviation
                        }
                    });
                    iconHTML.classList.add("cmp-segmenteditor__item-tag");
                }

                iconHTML.title = component.getTitle();

                return iconHTML;
            },

            /**
             * Binds Segment Editor events
             *
             * @private
             */
            _bindEvents: function() {
                var that = this;

                if (ns) {
                    that._elements.items.forEach(function(item) {
                        that._bindCloneAction(item);
                        that._bindSelectChange(item);
                    });
                    Coral.commons.ready(that._elements.add, function() {
                        that._elements.add.on("click", function() {
                            var editable = ns.editables.find(that._path)[0];
                            var children = editable.getChildren();

                            function getSelectListChangeEvent(onCloud) {
                                return (onCloud ? "click" : "coral-selectlist:change");
                            }

                            function getSelectListSelector(onCloud) {
                                return (onCloud ? "coral-list-item" : null);
                            }

                            function getSelectListItems(event, onCloud) {
                                return (onCloud ? ns.components.find(event.target.closest("coral-list-item").value)
                                    : ns.components.find(event.detail.selection.value));
                            }

                            // create the insert component dialog relative to a child item
                            // - against which allowed components are calculated.
                            if (children.length > 0) {
                                // display the insert component dialog
                                ns.edit.ToolbarActions.INSERT.execute(children[0]);

                                var insertComponentDialog = $(document).find(selectors.insertComponentDialog.self)[0];
                                var selectList = insertComponentDialog.querySelectorAll(selectors.insertComponentDialog.selectList)[0];
                                var onCloud = selectList.toString() === "Coral.List";

                                // next frame to ensure we remove the default event handler
                                Coral.commons.nextFrame(function() {
                                    selectList.off(getSelectListChangeEvent(onCloud));
                                    selectList.on(getSelectListChangeEvent(onCloud) + NS, getSelectListSelector(onCloud), function(event) {
                                        var resourceType = "";
                                        var templatePath = "";
                                        var components = "";

                                        insertComponentDialog.hide();

                                        components = getSelectListItems(event, onCloud);
                                        if (components.length > 0) {
                                            resourceType = components[0].getResourceType();
                                            templatePath = components[0].getTemplatePath();

                                            var item = that._elements.self.items.add(new Coral.Multifield.Item());

                                            // next frame to ensure the item template is rendered in the DOM
                                            Coral.commons.nextFrame(function() {
                                                var name = NN_PREFIX + Date.now();
                                                item.dataset["name"] = name;

                                                var selectCondition = item.querySelectorAll("coral-select" + selectors.item.condition)[0];
                                                selectCondition.name = "./" + name + "/" + PN_CONDITION;
                                                var inputCondition = item.querySelectorAll("input" + selectors.item.condition)[0];
                                                inputCondition.name = "./" + name + "/" + PN_CUSTOM_SEGMENT_CONDITION;

                                                var hiddenItemTitle = item.querySelectorAll("input[type='hidden']" + selectors.item.title)[0];
                                                hiddenItemTitle.name = "./" + name + "/" + PN_PANEL_TITLE;
                                                var customItemTitle = item.querySelectorAll("input[is='coral-textfield']" + selectors.item.title)[0];
                                                customItemTitle.name = "./" + name + "/" + PN_PANEL_TITLE;

                                                var hiddenItemResourceType = item.querySelectorAll(selectors.item.hiddenItemResourceType)[0];
                                                hiddenItemResourceType.value = resourceType;
                                                hiddenItemResourceType.name = "./" + name + "/" + PN_RESOURCE_TYPE;
                                                if (templatePath) {
                                                    var hiddenItemTemplatePath = item.querySelectorAll(selectors.item.hiddenItemTemplatePath)[0];
                                                    hiddenItemTemplatePath.value = templatePath;
                                                    hiddenItemTemplatePath.name = "./" + name + "/" + PN_COPY_FROM;
                                                }

                                                var itemIcon = item.querySelectorAll(selectors.item.icon)[0];
                                                var icon = that._renderIcon(components[0]);
                                                itemIcon.appendChild(icon);

                                                that._elements.self.trigger("change");
                                                that._bindSelectChange(item);
                                            });
                                        }
                                    });
                                });
                                // unbind events on dialog close
                                channel.one("coral-overlay:beforeclose", function() {
                                    selectList.off(getSelectListChangeEvent(onCloud) + NS);
                                });
                            }
                        });
                    });
                } else {
                    // editor layer unavailable, remove the insert component action
                    that._elements.add.parentNode.removeChild(that._elements.add);
                }

                Coral.commons.ready(that._elements.self, function() {
                    // As a reordering of the multifield also triggers the coral-collection:remove event we have to add
                    // a check for moved items so the prompt get only shown on a real remove action.
                    var movedItem;

                    that._elements.self.on("coral-multifield:itemorder", function(event) {
                        movedItem = event.detail.item.dataset["name"];
                    });

                    that._elements.self.on("coral-collection:remove", function(event) {
                        var name = event.detail.item.dataset["name"];
                        if (movedItem !== name) {
                            that._deletedChildren.push(name);
                        }
                    });

                    that._elements.self.on("coral-collection:add", function(event) {
                        var item = event.detail.item;
                        if (movedItem !== item.dataset["name"]) {
                            movedItem = undefined;
                        }
                        that._bindCloneAction(item);
                        that._bindSelectChange(item);
                    });
                });
            },

            _bindSelectChange: function(item) {
                var selectCondition = item.querySelectorAll("coral-select" + selectors.item.condition)[0];
                selectCondition.on("change" + NS, function(event) {
                    var customSegment = $(item).find(selectors.item.custom).adaptTo("foundation-toggleable");
                    var $customSegmentName = $(item).find("input[type='text']" + selectors.item.title).adaptTo("foundation-field");
                    var $customSegmentCondition = $(item).find("input[type='text']" + selectors.item.condition).adaptTo("foundation-field");
                    var hiddenItemTitle = item.querySelectorAll("input[type='hidden']" + selectors.item.title)[0];
                    var $hiddenItemTitle = $(hiddenItemTitle).adaptTo("foundation-field");
                    if (event.target.selectedItem.value === "custom") {
                        $customSegmentName.setValue("");
                        $customSegmentCondition.setValue("");
                        customSegment.show();
                        $hiddenItemTitle.setDisabled(true);
                    } else {
                        customSegment.hide();
                        hiddenItemTitle.value = event.target.selectedItem.textContent;
                        $hiddenItemTitle.setDisabled(false);
                    }
                });
            },

            _bindCloneAction: function(item) {
                var that = this;
                var items = that._elements.self.items.getAll();
                var $cloneButton = $(item).find(selectors.item.clone);
                $cloneButton.off("click");
                $cloneButton.on("click", function(event) {
                    var clonedItem = event.target.closest("coral-multifield-item");
                    var setsize = items.indexOf(clonedItem);
                    var insertBefore;
                    if (items.length >= setsize + 1) {
                        insertBefore = items[setsize + 1];
                    } else {
                        insertBefore = null;
                    }
                    var item = that._elements.self.items.add(new Coral.Multifield.Item(), insertBefore);
                    Coral.commons.nextFrame(function() {
                        that._setClonedItemProperties(item, clonedItem);
                    });
                });
            },

            _setClonedItemProperties: function(item, clonedItem) {
                var clonedIcon = clonedItem.querySelectorAll(selectors.item.icon)[0].firstElementChild.cloneNode(true);
                var clonedItemName = clonedItem.querySelectorAll(selectors.item.title)[0];
                clonedItemName = $(clonedItemName).adaptTo("foundation-field").getName();
                clonedItemName = clonedItemName.substring(2, clonedItemName.lastIndexOf("/"));

                var name = NN_PREFIX + Date.now();
                item.dataset["name"] = name;
                item.dataset["copy"] = clonedItemName;

                var selectCondition = item.querySelectorAll("coral-select" + selectors.item.condition)[0];
                selectCondition.name = "./" + name + "/" + PN_CONDITION;
                var inputCondition = item.querySelectorAll("input" + selectors.item.condition)[0];
                inputCondition.name = "./" + name + "/" + PN_CUSTOM_SEGMENT_CONDITION;

                var hiddenItemTitle = item.querySelectorAll("input[type='hidden']" + selectors.item.title)[0];
                hiddenItemTitle.name = "./" + name + "/" + PN_PANEL_TITLE;
                var customItemTitle = item.querySelectorAll("input[is='coral-textfield']" + selectors.item.title)[0];
                customItemTitle.name = "./" + name + "/" + PN_PANEL_TITLE;

                var itemIcon = item.querySelectorAll(selectors.item.icon)[0];
                itemIcon.append(clonedIcon);
            },

            /**
             * Reads the current state and updates ordered children cache
             *
             * @private
             */
            _processChildren: function() {
                this._orderedChildren = [];
                this._copiedChildren = [];
                var items = this._elements.self.items.getAll();

                for (var i = 0; i < items.length; i++) {
                    var name = items[i].dataset["name"];
                    var copy = items[i].dataset["copy"];
                    this._orderedChildren.push(name);
                    if (copy) {
                        this._copiedChildren.push(name + ":" + copy);
                    }
                }
                this._elements.order.value = this._orderedChildren.join();
                this._elements.delete.value = this._deletedChildren.join();
                this._elements.copy.value = this._copiedChildren.join();
            }
        };
    })();

    /**
     * Initializes Segment Editors as necessary on content loaded event
     */
    channel.on("foundation-contentloaded", function(event) {
        $(event.target).find(selectors.self).each(function() {
            // prevent multiple initialization
            if ($(this).data("SegmentEditor") === undefined) {
                new SegmentEditor({
                    el: this
                });
            }
        });
    });

    /**
     * Form pre-submit handler to process child updates
     */
    $(window).adaptTo("foundation-registry").register("foundation.form.submit", {
        selector: "*",
        handler: function(form) {
            // one children editor per form
            var el = form.querySelectorAll(selectors.self)[0];
            var segmentEditor = $(el).data("segmentEditor");
            if (segmentEditor) {
                return segmentEditor.update();
            } else {
                return $.Deferred().resolve();
            }
        }
    });

}(jQuery, Granite.author, jQuery(document), this));
