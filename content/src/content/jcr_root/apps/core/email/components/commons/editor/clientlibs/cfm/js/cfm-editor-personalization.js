/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2021 Adobe
 ~
 ~ Licensed under the Apache License, Version 2.0 (the "License");
 ~ you may not use this file except in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~     http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing, software
 ~ distributed under the License is distributed on an "AS IS" BASIS,
 ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ~ See the License for the specific language governing permissions and
 ~ limitations under the License.
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

(function(ns) {
    "use strict";

    var PERSONALIZATION_PLUGIN_ID = "personalizationplugin";
    var CMD_INSERT_VARIABLE = "insertvariable";

    extendStyledTextEditor();

    function extendStyledTextEditor() {
        var origFn = ns.StyledTextEditor.prototype._start;

        ns.StyledTextEditor.prototype._start = function() {
            addPlugin(this);
            origFn.call(this);
        };
    }

    function addPlugin(editor) {
        var config = editor.$editable.data("config");

        config.rtePlugins[PERSONALIZATION_PLUGIN_ID] = {
            features: "*"
        };
        config.uiSettings.cui.inline.toolbar.push(PERSONALIZATION_PLUGIN_ID + "#" + CMD_INSERT_VARIABLE);
        config.uiSettings.cui.multieditorFullscreen.toolbar.push(PERSONALIZATION_PLUGIN_ID + "#" + CMD_INSERT_VARIABLE);
    }

}(window.Dam.CFM));
