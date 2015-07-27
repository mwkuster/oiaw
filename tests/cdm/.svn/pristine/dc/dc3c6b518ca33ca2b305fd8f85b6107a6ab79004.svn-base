try {
AJS.Confluence.EditorLoader = (function() {
    var _loadingTimeout = 12000;
    
    var editorLoadingStatus = {
        loaded: false,
        inProgress: false,
        errorMessage: null,
        
        started: function() {
            this.inProgress = true;
        },
        
        successful: function() {
            this.inProgress = false;
            this.loaded = true;
        },
        
        error: function(message) {
            this.inProgress = false;
            this.errorMessage = message;
        },
        
        /**
         * Should we attempt to load or is there a load already in progress, already completed 
         * or previously failed?
         * 
         * @return true if loading should be attempted.
         */
        allowLoad : function() {
            return !this.loaded
                && !this.inProgress
                && this.errorMessage == null;
        }
    };
    
    /**
     * An object that tracks events that should change the state of the editor which occur
     * when the editor is deactivated. These changes will apply to the editor when it is
     * next activated.
     */
    var stateChangeEventListener = {
        _listening: false,
            
        _queuedHandlers: [],
            
        _watchHandler: function() {
            Confluence.Editor.UI.toggleWatchPage(false);            
        },
        
        _unwatchHandler: function() {
            Confluence.Editor.UI.toggleWatchPage(true);
        },
        
        _createQueueAdder: function(handler) {
            return function() {
                if (stateChangeEventListener._listening) {
                    stateChangeEventListener._queuedHandlers.push(handler);
                }
            }  
        },
            
        /**
         * bind to all the relevant events. 
         */
        bind: function() {
            AJS.bind("watchpage.pageoperation", this._createQueueAdder(this._watchHandler));
            AJS.bind("unwatchpage.pageoperation", this._createQueueAdder(this._unwatchHandler));
        },
        
        /**
         * @param listening if true then listen and queue handlers (i.e. the editor is deactivated). If false
         * then ignore any events. The editor is active and will handle them itself.
         */
        setListening: function(listening) {
            this._listening = listening;
        },
        
        /**
         * Apply and then clear all the queued handlers. 
         */
        applyHandlers: function() {
            var handler = this._queuedHandlers.pop();
            while (handler) {
                handler();
                handler = this._queuedHandlers.pop();
            }
        }
    };
    
    stateChangeEventListener.setListening(true);
    stateChangeEventListener.bind();
    
    /** An array of functions to be called when an Editor load completes successfully. */
    var onLoadSuccess = [];

    /** An array of functions to be called when an Editor load fails. */
    var onLoadError = [];
            
    /**
     * Check if a _load should be allowed and make the appropriate callback if it shouldn't.
     * 
     * @return true if load is guarded (shouldn't be allowed); false if a load should be permitted.
     */
    var loadGuard = function(successCallback, errorCallback) {
        if (!editorLoadingStatus.allowLoad()) {
            if (editorLoadingStatus.errorMessage) {
                if (errorCallback) {
                    errorCallback(editorLoadingStatus.errorMessage);
                } else {
                    AJS.log("EditorLoader: loadGuard - previous load failed.");
                }
            } else if (editorLoadingStatus.inProgress) {
                // record the other callbacks for later (if they were supplied)
                if (successCallback) {
                    onLoadSuccess.push(successCallback);                        
                }
                
                if (errorCallback) {
                    onLoadError.push(errorCallback);
                }
            } else if (editorLoadingStatus.loaded) {
                if (successCallback) {
                    successCallback();                        
                } else {
                    AJS.log("EditorLoader: loadGuard - editor is already loaded.");
                }
            }
            
            return true;
        }            
    };
    
    /**
     * Note, this methods doesn't do any encoding so don't add anything here you shouldn't.
     */
    var appendUrlParameter = function(url, name, value) {
        if (url.indexOf("?") == -1) {
            url += "?";
        } else {
            url += "&";
        }
        
        return url + name + "=" + value;
    };
    
    /**
     * @return an object with two properties; jsUrls is an array of Javascript URLs and cssTags is an array of HTML formatted CSS
     * Link elements (including conditional comments)
     */
    var getResources = function() {
        // TODO conditionals are basically comments - is inserting comment nodes into the DOM 
        // a valid way to load them. Test on IE, etc
        var resourceTags = AJS.Meta.get("editor.loader.resources");
        var $resources = AJS.$(resourceTags);
        var jsUrls = [];
        var cssTags = []; // link elements and comments containing conditional CSS
        
        // Separate the resources into CSS tags and JS urls.
        for (var i = 0; i < $resources.length; i++) {
            var resource = $resources[i];
            // nodeType 8 is Node.COMMENT_NODE
            if (resource.nodeType == 8 && resource.nodeValue.indexOf("text/css") != -1) {
                cssTags.push(resource);
            } else if (resource.nodeType == 1) {
                if (resource.nodeName == "LINK") {
                    cssTags.push(resource);
                } else if (resource.nodeName == "SCRIPT" && resource.getAttribute("type") == "text/javascript") {
                    var url = appendUrlParameter(resource.src, "trycatchwrap", "true");
                    jsUrls.push(url);
                }
            }
        }
        
        return {
            "jsUrls": jsUrls,
            "cssTags" : cssTags
        };
    };
    
    /**
     * Derive the base url for the Editor based on the array of urls provided.
     * 
     * @param urls an array of Javascript urls for the Editor
     * @return the baseUrl.
     */
    var getBaseUrl = function(urls) {
        var baseUrl = null;
        for (var i = 0; i < urls.length && baseUrl == null; i++) {
            var url = urls[i];
            // try to use part of a URL that would indicate this is definitely the editor scripts (but which couldn't be
            // confused with context part, etc)
            if (/editor,/.test(url)) {
                baseUrl = url;
            }
        }
        
        return baseUrl;
    };
    
    /**
     * Load the HTML and resources required by the Editor. (Note that the Editor is not necessarily initialised unless
     * the provided callback does it.)
     * 
     * @param successCallback a function called if the Editor is successfully loaded.
     * @param errorCallback a function called if there is a failure while loading the Editor (takes a message string parameter).
     */
    var loadEditor = function(successCallback, errorCallback) {
        if (loadGuard(successCallback, errorCallback)) {
            return;
        }
        
        editorLoadingStatus.started();
        
        if (successCallback) {
            onLoadSuccess.push(successCallback);                        
        }
        
        if (errorCallback) {
            onLoadError.push(errorCallback);
        }            
        
        var resources = getResources();
            
        AJS.Meta.set("rte.src.url", getBaseUrl(resources.jsUrls));

        // An object providing tracking of the various AJAX requests involved in loading
        var loadTracker = {
            countDown: resources.jsUrls.length + 1, // the number of scripts to be loaded plus the template
            errorMessages: [],
            success: function() {
                this.loaded();
            },
            
            error: function(message) {
                this.errorMessages.push(message);
                this.loaded();
            },
            
            loaded: function() {
                this.countDown--;
                if (this.countDown == 0) {
                    if (this.errorMessages.length == 0) {
                        editorLoadingStatus.successful();
                        AJS.log("EditorLoader: Finished loading the editor.");
                        
                        AJS.$.each(onLoadSuccess, function(i, func) {
                            func();
                        });                                            
                    } else {
                        var joinedMessages = this.errorMessages.join();
                        editorLoadingStatus.error(joinedMessages);   
                        AJS.log("EditorLoader: Error while loading the editor: " + joinedMessages);
                        
                        AJS.$.each(onLoadError, function(i, func) {
                            func(joinedMessages);
                        });                                        
                    }

                    AJS.log("EditorLoader: all 'after load' callbacks have been called.");

                    // can clear callback arrays - we don't need them any more
                    onLoadSuccess = null;
                    onLoadError = null;                        
                }
            } 
        };
        
        var loadCallback = function() {
            loadTracker.success();
        };
        
        var loadErrorCallback = function(jqXHR, textStatus, errorThrown) {
            var message = "";
            if (textStatus) {
                message = textStatus;
            }
            
            if (errorThrown) {
                message = message + ": " + errorThrown;                    
            }
            
            loadTracker.error(message);
        };
        
        var originalTimeout = AJS.$.ajaxSetup().timeout;
        AJS.$.ajaxSetup({ timeout: _loadingTimeout });
        
        // Create a hidden container to load the Editor DOM into
        var $preloadContainer = getPreloadContainer(); 
        AJS.$("body").append($preloadContainer);

        // Load the Editor template
        $preloadContainer.load(Confluence.getContextPath() + "/plugins/editor-loader/editor.action",
                { 
                    pageId: AJS.Data.get("page-id"), 
                    spaceKey: AJS.Data.get("space-key"), 
                    atl_after_login_redirect: window.location.pathname // the URL that an anonymous user will be redirect to after logging in
                },
                function(response, status, xhr) {
                    if (status == "success" || status == "notmodified") {
                        // move any metadata into the head (which is the only legal place for meta tags).
                        var metadataTemplate = AJS.renderTemplate("dynamic-editor-metadata");
                        AJS.$("head").append(metadataTemplate);
                        AJS.log("EditorLoader: Finished loading the editor template.");        
                        loadCallback();                            
                    } else {
                        loadErrorCallback(xhr, "Error loading the Editor template: " + xhr.status + " - " + xhr.statusText, null);
                    }
                });
        
        // CONFDEV-7632 - with web resource batching turned off (or simply more than one script) we cannot 
        // load all scripts asynchronously. Scripts may have cross dependencies so we pretty much need to load 
        // them in serial in the same order the would be found in the batch for them to work.
        AJS.log("EditorLoader: " + resources.jsUrls.length + " scripts to be loaded.");

        var originalCacheSetting = AJS.$.ajaxSetup().cache;
        AJS.$.ajaxSetup({ cache: true });
        
        var ajaxSettings = {
            dataType: "script",
            error: loadErrorCallback,
            success: loadCallback                
        };
        
        if (AJS.$.browser.msie) {
            // without this setting window.execScript would be used which makes debugging
            // very difficult.
            ajaxSettings.crossDomain = true;
        }
        
        if (resources.jsUrls.length > 1) {
            var $head = AJS.$("head");
            AJS.$.each(resources.jsUrls, function(index, jsUrl) {
                var $script = AJS.$("<script></script>");
                $script.attr("src", jsUrl);
                $head.append($script);
                setTimeout(loadCallback); // calling loadCallback in the same 'event' seems to cause weird problems in Firefox on Linux.            
            });
        } else {
            // To avoid CONFDEV-8038 prefer this mechanism for the normal (single batched resource) case.
            AJS.$.each(resources.jsUrls, function(index, jsUrl) {
                ajaxSettings.url = jsUrl;
                AJS.$.ajax(ajaxSettings);            
            });            
        }

        AJS.$.ajaxSetup({
            cache: originalCacheSetting,
            timeout: originalTimeout 
        });
        
        // TODO don't append the CSS until the Editor is actually activated to avoid risk of style clashes
        AJS.$.each(resources.cssTags, function(index, tag) {
            AJS.$("head").append(tag);
        });
        
        AJS.log("EditorLoader: Finished inserting the editor CSS tags.");
    };
    
    /**
     * Create the callback used to transfer the editor DOM and initialise the editor.
     * This callback will only be used the first time the editor is activated. Subsequent
     * activations will make use of _createReactivationCallback.
     * 
     * @param $container jQuery wrapped Element which the Editor HTML will be appended to.
     * @param successCallback the client provided callback to be called once the editor is ready.
     * A client would typically use this callback to reveal the editor and perform any other UI
     * related functionality.
     * @returns {Function}
     */
    var createFirstActivationCallback = function($container, successCallback) {
        return function() {
            var $preloadContainer = getPreloadContainer();
            $container.append($preloadContainer);
            $preloadContainer.show();

            AJS.bind("init.rte", function() {
                successCallback();
                stateChangeEventListener.setListening(false);
                stateChangeEventListener.applyHandlers();
                AJS.trigger("add-bindings.keyboardshortcuts");
                AJS.trigger("active.dynamic.rte");
            });                
            
            AJS.Rte.BootstrapManager.initialise();

            Confluence.Editor.removeAllSubmitHandlers(); // we will use AJAX not form submission (posts) so prevent the submit.  
            Confluence.Editor.addSubmitHandler(function(e) {
                e.preventDefault()
                return false;
            });
        };            
    };
    
    /**
     * Create the callback used to activate the editor when it has been active previously.
     * 
     * @param $container
     * @param successCallback
     * @returns {Function}
     */
    var createReactivationCallback = function($container, successCallback) {
        return function() {
            var $preloadContainer = getPreloadContainer();
            if ($preloadContainer.parent()[0] != $container[0]) {
                $container.append($preloadContainer);                    
            }
            
            $preloadContainer.show();
            AJS.Rte.getEditor().focus();
            successCallback();
            stateChangeEventListener.setListening(false);
            stateChangeEventListener.applyHandlers();            
            AJS.trigger("add-bindings.keyboardshortcuts");
            AJS.trigger("active.dynamic.rte");
        };
    };
    
    /**
     * @returns the jQuery wrapped Element that contains the editor DOM, or create and return a new hidden div
     * if there is none found
     */
    var getPreloadContainer = function() {
        var $container = AJS.$("#editor-preload-container");
        if (!$container.length) {
            $container = AJS.$("<div id=\"editor-preload-container\" style=\"display: none;\"></div>");
        }
        
        return $container;
    };
    
    return {
        /** The maximum wait in milliseconds for the Editor to load */
        loadingTimeout: _loadingTimeout,
        
        /**
         * @returns true if there is already an active editor; otherwise false
         */
        isEditorActive: function() {
           var $container = AJS.$("#editor-preload-container");
           return $container.length && $container.is(":visible");
        },
        
        /**
         * Load the Editor into a hidden Element on the page if it hasn't already been loaded. 
         * The Editor is not initialised, its HTML, CSS and Javascript is simply loaded ready for
         * later activation.
         */
        load: loadEditor,
        
        /**
         * Activate the editor upon the given container.
         * <p/>
         * If the editor has not finished loading when this method is called then the activation
         * will occur asynchronously (when the load completes). If the editor is already loaded
         * then the success callback will occur immediately.
         * <p/>
         * If the editor has failed to load then the errorCallback will be called.
         * <p/>
         * Note that the Editor DOM is simply transferred from a current hidden location to the supplied
         * container. If you want to perform any kind of transition then you should ensure the container is
         * hidden and perform the reveal in the successCallback you provide.
         * 
         * @param container jQuery wrapped Element to display the Editor inside.
         * @param successCallback a function called once the Editor is activated and initialisation is complete.
         * @param errorCallback a function called if there is a failure while activating the Editor (takes a message string parameter).
         */
        activate: function($container, successCallback, errorCallback) {
            if (this.isEditorActive()) {
                return;
            }
            
            if (editorLoadingStatus.errorMessage) {
                errorCallback(editorLoadingStatus.errorMessage);
                return;
            }
            
            AJS.trigger("remove-bindings.keyboardshortcuts");
            // shortcuts will be added again in the callback below
            
            var loadCallback = null;
            if (editorLoadingStatus.loaded && AJS.Rte && AJS.Rte.BootstrapManager && AJS.Rte.BootstrapManager.isInitComplete()) {
                loadCallback = createReactivationCallback($container, successCallback);
            } else {
                loadCallback = createFirstActivationCallback($container, successCallback);
            }
            
            loadEditor(loadCallback, errorCallback);
        },
  
        /**
         * Remove the editor from the container it was previously activated upon. deactivate must be called before an Editor can be activated
         * on another container.
         */
        deactivate: function() {
            if (this.isEditorActive()) {
                stateChangeEventListener.setListening(false);
                
                // remove all keyboard bindings and then re-bind so that we don't have editor bindings 
                // (our context enablement calculation will differ once the editor is no longer visible)
                AJS.trigger("remove-bindings.keyboardshortcuts");
                
                // This interferes with the key buffering during launch and since ajax submission is disabled
                // it is not necessary at the moment.
                // AJS.Rte.Content.setHtml("");
                // AJS.Rte.Content.editorResetContentChanged();
                var $preloadContainer = getPreloadContainer();
                $preloadContainer.hide();
                
                // Move the preload container to the body of the document in case the client plans to do 
                // anything untoward with the container now.
                // TODO You can't actually move the editor's iframe around: http://stackoverflow.com/a/2542043 so I need a new plan
                // AJS.$("body").append($preloadContainer);
                
                AJS.trigger("add-bindings.keyboardshortcuts");
            }
        },
        
        /**
         * @return the immediate parent of the currently active editor as a jQuery wrapped Element. If an editor is not
         * currently active then null will be returned.
         */
        getCurrentContainer: function() {
            if (this.isEditorActive()) {
                return getPreloadContainer().parent();
            } else {
                return null;
            }
        }        
    };
})();


} catch (err) {
    if (console && console.log) {
        console.log("Error loading resource (46883)");
        console.log(err);
    }
}

try {
// TODO Move this to core Confluence and modify it to also take a TinyMCE Editor as a block object
// This could then also be used by the find and replace plugin (which is currently duplicating
// most of this).
AJS.Confluence.BlockAndBuffer = {
        
        _cancelKeyboardFunction: function(e) {
            e.preventDefault();
            e.stopPropagation();
        },
        
        /* Handle UTF-16 surrogate pair character codes */
        _surrogatePairFixedFromCharCode: function(code) {  
            if (code > 0xFFFF) {  
                code -= 0x10000;  
                return String.fromCharCode(0xD800 + (code >> 10), 0xDC00 +  (code & 0x3FF));  
            }  
            else {  
                return String.fromCharCode(code);  
            }  
        },
        
        /*
         * Buffer any character codes being typed and also prevent
         * the event that is instigating them. 
         */
        _bufferTextFunction: function(e, buffer) {
            AJS.Confluence.BlockAndBuffer._cancelKeyboardFunction(e);
            
            var keyCode = e.which;
            // IE8 doesn't have charCode for keypress event
            if (!keyCode) {
                keyCode = e.charCode ? e.charCode : e.keyCode;
            }

            // Firefox and Opera wrongly raise keypress for control characters
            if (keyCode < 48) {
                return;
            }

            buffer.push(keyCode);
        },
        
        
        /**
         * @return the buffered text the user may have entered
         */
        _unblock: function($jq, keycodeBuffer, blockFunc) {
            
            $jq.unbind("keypress", blockFunc);
            
            var bufferedText = "";
            for (var i = 0; i < keycodeBuffer.length; i++) {
                bufferedText += AJS.Confluence.BlockAndBuffer._surrogatePairFixedFromCharCode(keycodeBuffer[i]);
            }

            return bufferedText;
        },
        
        /* -------------------------- Public API -------------------------- */
        
        /**
         * Block keys on the supplied jQuery DOM object. 
         * 
         * @param $jq jQuery wrapped Element.
         * @return the zero argument unblock function you should run when you want to cancel the block. This will return
         * the buffer as a String.
         */
        block: function($jq) {
            keycodeBuffer = [];

            var bufferText = function(e) {
                AJS.Confluence.BlockAndBuffer._bufferTextFunction(e, keycodeBuffer);
                return false;
            };
            
            $jq.keypress(bufferText);
            
            return function() {
                return AJS.Confluence.BlockAndBuffer._unblock($jq, keycodeBuffer, bufferText);
            };
        }
};
} catch (err) {
    if (console && console.log) {
        console.log("Error loading resource (54854)");
        console.log(err);
    }
}

try {
// This file was automatically generated from comments.soy.
// Please don't edit this file by hand.

if (typeof Confluence == 'undefined') { var Confluence = {}; }
if (typeof Confluence.Templates == 'undefined') { Confluence.Templates = {}; }
if (typeof Confluence.Templates.Comments == 'undefined') { Confluence.Templates.Comments = {}; }


Confluence.Templates.Comments.displayReplyEditorLoadingContainer = function(opt_data, opt_sb) {
  var output = opt_sb || new soy.StringBuilder();
  output.append('<ol class="comment-threads"><li class="comment-thread">');
  Confluence.Templates.Comments.displayCommentEditorCommon({comment: {'ownerId': opt_data.contentId, 'parentId': opt_data.parentCommentId}, commenter: opt_data.commenter, state: 'loading', mode: 'reply', context: opt_data.context}, output);
  output.append('</li></ol>');
  return opt_sb ? '' : output.toString();
};


Confluence.Templates.Comments.displayTopLevelCommentEditorPlaceholder = function(opt_data, opt_sb) {
  var output = opt_sb || new soy.StringBuilder();
  Confluence.Templates.Comments.displayCommentEditorCommon({comment: {'ownerId': opt_data.contentId}, commenter: opt_data.commenter, state: 'placeholder', mode: 'add', context: opt_data.context}, output);
  return opt_sb ? '' : output.toString();
};


Confluence.Templates.Comments.editorLoadErrorMessage = function(opt_data, opt_sb) {
  var output = opt_sb || new soy.StringBuilder();
  output.append('<p>', soy.$$escapeHtml(opt_data.message), '</p><p><a href="', soy.$$escapeHtml(opt_data.fallbackUrl), '">', soy.$$escapeHtml("Try again"), '</a></p>');
  return opt_sb ? '' : output.toString();
};


Confluence.Templates.Comments.displayCommentEditorCommon = function(opt_data, opt_sb) {
  var output = opt_sb || new soy.StringBuilder();
  output.append('<div class="quick-comment-container comment ', soy.$$escapeHtml(opt_data.mode), '">');
  Confluence.Templates.Comments.userLogo({userInfo: opt_data.commenter, context: opt_data.context}, output);
  output.append('<div class="quick-comment-vertical-spacer"></div><div class="quick-comment-body"><div class="quick-comment-loading-container" style="display:', (opt_data.state == 'loading') ? 'block' : 'none', ';"><p class="quick-comment-loading-message">', soy.$$escapeHtml("Loading the Editor"), '</p></div><form style="display:', (opt_data.state == 'loading') ? 'none' : 'block', ';" class="quick-comment-form" method="post" ');
  switch (opt_data.mode) {
    case 'add':
      output.append('name="inlinecommentform" action="', soy.$$escapeHtml(opt_data.context.contextPath), '/pages/doaddcomment.action?pageId=', soy.$$escapeHtml(opt_data.comment.ownerId), '"');
      break;
    case 'edit':
      output.append('name="editcommentform" action="', soy.$$escapeHtml(opt_data.context.contextPath), '/pages/doeditcomment.action?pageId=', soy.$$escapeHtml(opt_data.comment.ownerId), '&commentId=', soy.$$escapeHtml(opt_data.comment.id), '"');
      break;
    case 'reply':
      output.append('name="threadedcommentform"  action="', soy.$$escapeHtml(opt_data.context.contextPath), '/pages/doaddcomment.action?pageId=', soy.$$escapeHtml(opt_data.comment.ownerId), '&parentId=', soy.$$escapeHtml(opt_data.comment.parentId), '"');
      break;
  }
  output.append(' ><div title="', soy.$$escapeHtml("Add a Comment"), '" class="quick-comment-prompt"><span>', soy.$$escapeHtml("Write a comment\u2026"), '</span></div></form></div></div>');
  return opt_sb ? '' : output.toString();
};


Confluence.Templates.Comments.userLogo = function(opt_data, opt_sb) {
  var output = opt_sb || new soy.StringBuilder();
  output.append('<p class="comment-user-logo">', (opt_data.userInfo.userName == null) ? '<img class="userLogo logo anonymous" src="' + soy.$$escapeHtml(opt_data.context.staticResourceUrlPrefix) + '/images/icons/profilepics/anonymous.png" alt="' + soy.$$escapeHtml("User icon") + ': ' + soy.$$escapeHtml("Anonymous") + '" title="' + soy.$$escapeHtml("Anonymous") + '">' : (opt_data.userInfo.profilePicture.isDefault) ? '<a class="userLogoLink" data-username="' + soy.$$escapeHtml(opt_data.userInfo.userName) + '" href="' + soy.$$escapeHtml(opt_data.context.contextPath) + '/users/editmyprofilepicture.action" title="' + soy.$$escapeHtml("Add a picture of yourself") + '"><img class="userLogo logo defaultLogo" src="' + soy.$$escapeHtml(opt_data.context.staticResourceUrlPrefix) + '/images/icons/profilepics/add_profile_pic.png" alt="' + soy.$$escapeHtml("User icon") + ': ' + soy.$$escapeHtml("Add a picture of yourself") + '"></a>' : '<a class="userLogoLink" data-username="' + soy.$$escapeHtml(opt_data.userInfo.userName) + '" href="' + soy.$$escapeHtml(opt_data.context.contextPath) + '/display/~' + soy.$$escapeUri(opt_data.userInfo.userName) + '"><img class="userLogo logo" src="' + soy.$$escapeHtml(opt_data.context.contextPath) + soy.$$escapeHtml(opt_data.userInfo.profilePicture.path) + '" alt="' + soy.$$escapeHtml("User icon") + ': ' + soy.$$escapeHtml(opt_data.userInfo.userName) + '" title="' + soy.$$escapeHtml(opt_data.userInfo.userName) + '"></a>', '</p>');
  return opt_sb ? '' : output.toString();
};


Confluence.Templates.Comments.displayComment = function(opt_data, opt_sb) {
  var output = opt_sb || new soy.StringBuilder();
  if (opt_data.comment.parentId == 0 && opt_data.firstReply) {
    output.append('<div id="comments-section" class="pageSection group"><div class="section-header"><h2 id="comments-section-title" class="section-title"><a href="#" class="comments-show-hide comments-showing" name="comments">', soy.$$escapeHtml("1 Comment"), '</a></h2><a title="', soy.$$escapeHtml("Hide/Show Comments"), '" href="#" class="comments-show-hide icon comments-showing icon-section-opened">', soy.$$escapeHtml("Hide/Show Comments"), '</a>');
    Confluence.Templates.Comments.commentThread({comment: opt_data.comment, commenter: opt_data.commenter, highlight: opt_data.highlight, topLevel: true, context: opt_data.context}, output);
    output.append('</div></div>');
  } else if (opt_data.firstReply) {
    Confluence.Templates.Comments.commentThread({comment: opt_data.comment, commenter: opt_data.commenter, highlight: opt_data.highlight, topLevel: false, context: opt_data.context}, output);
  } else {
    Confluence.Templates.Comments.commentThreadItem({comment: opt_data.comment, commenter: opt_data.commenter, highlight: true, context: opt_data.context}, output);
  }
  return opt_sb ? '' : output.toString();
};


Confluence.Templates.Comments.commentThread = function(opt_data, opt_sb) {
  var output = opt_sb || new soy.StringBuilder();
  output.append('<ol class="comment-threads', (opt_data.topLevel) ? ' top-level" id="page-comments' : '', '">');
  Confluence.Templates.Comments.commentThreadItem(opt_data, output);
  output.append('</ol>');
  return opt_sb ? '' : output.toString();
};


Confluence.Templates.Comments.commentThreadItem = function(opt_data, opt_sb) {
  var output = opt_sb || new soy.StringBuilder();
  output.append('<li id="comment-thread-', soy.$$escapeHtml(opt_data.comment.id), '" class="comment-thread">');
  Confluence.Templates.Comments.commentView(opt_data, output);
  output.append('</li>');
  return opt_sb ? '' : output.toString();
};


Confluence.Templates.Comments.commentView = function(opt_data, opt_sb) {
  var output = opt_sb || new soy.StringBuilder();
  output.append('<div class="comment', (opt_data.highlight == true) ? ' focused' : '', '" id="comment-', soy.$$escapeHtml(opt_data.comment.id), '">');
  Confluence.Templates.Comments.userLogo({userInfo: opt_data.commenter, context: opt_data.context}, output);
  output.append('<p class="date" title="', soy.$$escapeHtml("just a moment ago"), '">', soy.$$escapeHtml("just a moment ago"), '</p><div class="comment-header"><h4 class="author">', (opt_data.commenter.userName == null) ? soy.$$escapeHtml("Anonymous") : '<a href="' + soy.$$escapeHtml(opt_data.context.contextPath) + '/display/' + soy.$$escapeUri(opt_data.commenter.userName) + '" class="url fn confluence-userlink" data-username="' + soy.$$escapeHtml(opt_data.commenter.userName) + '">' + soy.$$escapeHtml(opt_data.commenter.displayName) + '</a>', '</h4></div><div class="comment-body"><div class="comment-content wiki-content">', opt_data.comment.html, '</div><div class="comment-actions">');
  Confluence.Templates.Comments.displayCommentActions({section: 'secondary', actions: opt_data.comment.secondaryActions, commentId: opt_data.comment.id}, output);
  Confluence.Templates.Comments.displayCommentActions({section: 'primary', actions: opt_data.comment.primaryActions, commentId: opt_data.comment.id}, output);
  output.append('</div></div></div>');
  return opt_sb ? '' : output.toString();
};


Confluence.Templates.Comments.displayCommentActions = function(opt_data, opt_sb) {
  var output = opt_sb || new soy.StringBuilder();
  output.append('<ul class="comment-actions-', soy.$$escapeHtml(opt_data.section), '">');
  if (opt_data.actions != null) {
    var itemList223 = opt_data.actions;
    var itemListLen223 = itemList223.length;
    for (var itemIndex223 = 0; itemIndex223 < itemListLen223; itemIndex223++) {
      var itemData223 = itemList223[itemIndex223];
      output.append('<li ', (itemData223.style != null) ? ' class="' + soy.$$escapeHtml(itemData223.style) + '"' : '', '><a ', (itemData223.tooltip != null) ? ' title="' + soy.$$escapeHtml(itemData223.tooltip) + '"' : '', ' href="', soy.$$escapeHtml(itemData223.url), '" ', (itemData223.id) ? ' id="' + soy.$$escapeHtml(itemData223.id) + '-' + soy.$$escapeHtml(opt_data.commentId) + '"' : '', '><span>', soy.$$escapeHtml(itemData223.label), '</span></a></li>\n');
    }
  }
  output.append('</ul>');
  return opt_sb ? '' : output.toString();
};

} catch (err) {
    if (console && console.log) {
        console.log("Error loading resource (11984)");
        console.log(err);
    }
}

try {
// TODO this should be merged with Confluence.Comments from comments.js in the core product.
// At the moment it is kept separate because having it in a plugin is better for dev speed.
// This script is dependent on templates in comments.soy
Confluence.CommentDisplayManager = (function($) {

    var createTemplateInjectionContext = function() {
        return {
            "contextPath": AJS.Meta.get("context-path"),
            "staticResourceUrlPrefix": AJS.Meta.get("static-resource-url-prefix")
        };
    };
    
    var createTemplateParameters = function(commenter, comment, highlight) {
        return {
            "comment": comment,
            "commenter": commenter,
            "highlight": highlight,
            "context": createTemplateInjectionContext()
        };
    };
    
    return {

        /**
         * Update the comments section heading if it exists with the current number
         * of comments.
         */
        _updateCommentSectionTitle: function() {
            var $title = $("#comments-section-title");
            if ($title.length == 0) {
                return;
            }

            var count = this.commentCount();
            if (count == 1) {
                $("a", $title).text("1 Comment");
            } else {
                var text = AJS.format("{0} Comments", count);
                $("a", $title).text(text);
            }
        },
        
        /**
         * Add and focus the display on a new comment. If the comments section is not visible then it will be
         * automatically expanded.
         * 
         * @param commenter the person making the comment. This is an object with the following structure:
         * {
         *     userName:       (string)
         *     displayName:    (string)
         *     profilePicture: {
         *         isDefault: (boolean)
         *         path:      (string)
         *     }
         * }
         * @param comment the comment. This is an object with the following structure:
         * {
         *     id:               (number)
         *     html:             (string)
         *     ownerId:          (number)
         *     parentId:         (number) 0 indicates a top level comment
         *     primaryActions:    (array of actions) may be empty
         *     secondaryActions: (array of actions) may be empty
         * }
         * @param highlight true if you want the comment to appear highlighted
         * @param keepFocus true if you don't want to clear the focus
         * @returns a jQuery wrapped DOM Node which is the top container for the newly added comment.
         */
        addComment: function(commenter, comment, highlight, keepFocus) {
            var templateParams = createTemplateParameters(commenter, comment, highlight);
            
            if (!this.hasComments()) {
                templateParams.firstReply = true;
                var template = Confluence.Templates.Comments.displayComment(templateParams);
                AJS.$("#comments-section").prepend(template);
                Confluence.Comments.toggleComments(true);
                Confluence.Comments.bindShowHideToggle();
            } else {
                if (!this.isVisible()) {
                    Confluence.Comments.toggleComments(true);
                }
                
                var $appendLocation;
                
                if (comment.parentId == 0) {
                    templateParams.firstReply = false;
                    $appendLocation = AJS.$("#comments-section .comment-threads.top-level");                    
                } else {
                    var $commentThread = AJS.$("#comment-thread-" + comment.parentId);
                    var $replyThread = $commentThread.children(".commentThreads");
                    
                    if ($replyThread.length) {
                        templateParams.firstReply = false;
                        $appendLocation = $replyThread;
                    } else {
                        templateParams.firstReply = true;
                        $appendLocation = $commentThread;
                    }
                }
                if (!keepFocus) {
                    this.clearFocus();
                }
                var renderedTemplate = Confluence.Templates.Comments.displayComment(templateParams);
                $appendLocation.append(renderedTemplate);
                this._updateCommentSectionTitle();
            }
            
            Confluence.Comments.bindRemoveConfirmation(comment.id);
            var $insertedComment = this.getCommentNode(comment.id);
            // Scroll to the newly added comment.
            $insertedComment.scrollToElement();
            return $insertedComment;
        },        

        /**
         * Add or focus the display on a new comment, or update an existing comment.
         *
         * @see Confluence.CommentDisplayManager.addComment for more information.
         */
        addOrUpdateComment: function(commenter, comment, highlight, keepFocus) {
            var oldComment = this.getCommentNode(comment.id);
            if (oldComment) {
                oldComment.find('.comment-content').html(comment.html);
                if (!keepFocus) {
                    this.clearFocus();
                }
                if (highlight) {
                    oldComment.addClass('focused');
                }
                oldComment.scrollToElement();
                return oldComment;
            } else {
                return this.addComment.apply(this, arguments);
            }
        },

        /**
         * @return true if the comment section is visible.
         */
        isVisible : function() {
            return !$('#page-comments').hasClass("hidden");
        },
        
        /**
         * @return true if there are any comments displayed on the page.
         */
        hasComments: function() {
            return this.commentCount() > 0;
        },
        
        /**
         * @return the number of comments on the page.
         */
        commentCount: function() {
            return AJS.$("#comments-section .comment").not(".quick-comment-container").length;
        },
        
        /**
         * Remove the focus from all comments
         */
        clearFocus: function() {
            $(".comment").removeClass("focused");
        },
        
        /**
         * @param commentId the id of the comment required
         * @returns the jQuery wrapped DOM node for the top div of the identified comment or null if not found.
         */
        getCommentNode: function(commentId) {
            var $node = $("#comment-" + commentId);
            
            if (!$node.length) {
                return null;
            } else {
                return $node;
            }
        },
        
        /**
         * Get the id of the comment the indicated one is a reply to. If the comment is not a reply
         * then return 0.
         * 
         * @param commentId the id of the comment whos parent is required
         * @return the id of the parent comment or 0 if the comment is not a reply.
         */
        getParentId: function(commentId) {
            var $comment = getCommentNode(commentId);
            
            if ($comment != null) {
                var $parentComment = $comment.closest("div.comment");
                if ($parentComment.length) {
                    var idStr = $parentComment.attr("id");
                    var id = /\d+/.exec(idStr);
                    return parseInt(id);
                }
            }
            
            return 0;
        }
    };    
})(AJS.$);
} catch (err) {
    if (console && console.log) {
        console.log("Error loading resource (64973)");
        console.log(err);
    }
}

try {
AJS.$(function($) {

    $.fn.extend({
        scrollToElement: function() {
            if(!this.scrollWindowToElement()) {
                // try and scroll the closest overflow set parent (e.g. Documentation Theme)
                this.scrollOverflowContainerToElement();
            }
            return this;
        },

        /**
         * The default and the Easy Reader theme have scrollbars on the window and therefore this
         * method can make sure the supplied element is visible. However other themes don't scroll the
         * window so this method will return true if it successfully scrolls and false if it is unable
         * to move the window.
         */
        scrollWindowToElement: function() {
            var $element = this;
            function getScrollY() {
                if ('pageYOffset' in window) {  // all browsers, except IE before version 9
                    return window.pageYOffset;
                } else { // Internet Explorer before version 9
                    // we ignore zoom factor which was only an issue before IE8
                    return document.documentElement.scrollTop;
                }
            };

            var scrollY = getScrollY();

            var windowHeight;
            if (typeof(window.innerWidth) == 'number') {
                windowHeight = window.innerHeight;
            } else if (document.documentElement && document.documentElement.clientWidth) {
                // IE 6+ in 'standards compliant mode'
                windowHeight = document.documentElement.clientHeight;
            } else {
                // something old and creaky - just try to make sure the element will be visible and return true so we consider this successful
                $element[0].scrollIntoView(false);
                return true;
            }

            var elementVerticalPosition = $element.offset().top;
            var elementHeight = $element.height();

            var extra = 40; // the calculation seems to be off by 40 pixels - I don't know why (perhaps padding on $element?)

            if ((elementVerticalPosition + elementHeight + extra) > scrollY + windowHeight) {
                $element[0].scrollIntoView(false); // align to the bottom of the viewport
                window.scrollBy(0, extra);
                return scrollY != getScrollY(); // did we successfully scroll the window?
            } else {
                // no scrolling was necessary
                return true;
            }
        },

        /**
         * Find the closest parent with the CSS property overflow set to either 'scroll' or 'auto' and
         * scroll this to show the element.
         *
         * @return true if successfully found a parent to scroll.
         */
        scrollOverflowContainerToElement: function() {
            var $element = this;
            var $parent = null;

            $element.parents().each(function(index) {
                var overflow = AJS.$(this).css("overflow");
                if (overflow == "auto" || overflow == "scroll") {
                    $parent = AJS.$(this);
                    return false;
                }
            });

            if (!$parent) {
                return false;
            }

            var height = $parent.height();

            var extra = 40; // the calculation seems to be off by 40 pixels - I don't know why (perhaps padding on $element?)
            var elementVerticalPosition = $element.offset().top;
            var elementHeight = $element.height();
            var differential = height - (elementVerticalPosition + elementHeight + extra);

            if (differential < 0) {
                $parent[0].scrollTop = $parent[0].scrollTop - differential;
            }

            return true;
        }
    });
});
} catch (err) {
    if (console && console.log) {
        console.log("Error loading resource (56044)");
        console.log(err);
    }
}

try {
/**
 * Depends on AJS.Confluence.EditorLoader.
 */
AJS.$(function() {
    
    /**
     * An object that binds actions to the save bar as necessary
     */
    SaveBarBinder = {
            _saveHandler: null,
            _cancelHandler: null,
            
            bind: function(saveHandler, cancelHandler) {
                if (!SaveBarBinder.hasBound()) {   
                    SaveBarBinder._saveHandler = saveHandler;
                    Confluence.Editor.addSaveHandler(saveHandler);
                    SaveBarBinder._cancelHandler = cancelHandler;
                    Confluence.Editor.addCancelHandler(cancelHandler);
                }
            },
            
            /**
             * @returns true if we have already bound save or cancel handlers. Otherwise false.
             */
            hasBound: function() {
                return SaveBarBinder._saveHandler != null || SaveBarBinder._cancelHandler != null;
            }
    };
    
    /**
     * A success handler that will reload the page focused on the new comment. 
     */
    var reloadPageSaveCommentHandler = function(data) {
        var baseUrl = getBaseUrl();
        baseUrl.addQueryParam("focusedCommentId", data.id);
        
        var reloadUrl = baseUrl.toString();
        window.location.href = baseUrl.toString() + "#comment-" + data.id;
    };
    
    /**
     * Return an object representing the base URL. 
     */
    var getBaseUrl = function() {
        // stripping ':' from protocol and '/' from pathname to handle cross browser inconsistency
        var baseUrl = window.location.protocol.replace(/:$/,"") + "://" + window.location.host + "/" +  window.location.pathname.replace(/^\//,""); 

        var search = window.location.search.replace(/^\?/,""); // drop the leading '?'
        search = search.replace(/\&?focusedCommentId=\d+/,"");
        search = search.replace(/^\&/,"");
        
        return {
            url : baseUrl,
            
            search: search,
            
            addQueryParam : function(name, value) {
                if (!this.search) {
                    this.search = name + "=" + value;
                } else {
                    this.search = this.search + "&" + name + "=" + value;
                }
            },
            
            toString: function() {
                return this.url + "?" + this.search;
            }
        };
    }
    
    var cancelHandler = function() {
        AJS.Rte.Content.editorResetContentChanged();
        window.location.reload();
    };
    
    /**
     * Common QuickComment functionality. 
     */
    var QuickComment = {
            
        /**
         * Activate the dynamic editor on the supplied container.
         * 
         * @param $container the container containing the necessary structure to activate the 
         * editor within.
         * @param saveHandler the function to be called when save is activated on the editor
         * @param cancelHandler the function to be called when the editor is cancelled.
         * @param transition the function to be called to handle the UI involved in transitioning from placeholder
         * to editor. This function should take the parameters $form, $loadingContainer, $spacer.
         * @param fallbackUrl the url presented to the user if there is an error activating the editor
         */
        activateEditor: function($container, saveHandler, cancelHandler, transition, fallbackUrl) {
            var $form = AJS.$("form", $container);
            var $loadingContainer = AJS.$(".quick-comment-loading-container", $container);
            var $spacer = AJS.$(".quick-comment-vertical-spacer", $container);
            
            // Start the loading transition display
            var cancelLoadingMessage = transition($form, $loadingContainer, $spacer);
            
            var unblocker = AJS.Confluence.BlockAndBuffer.block(AJS.$(document));
            
            /**
             * Display the given error message inside the provided container.
             */
            var displayError = function(message, fallbackUrl) {
                $loadingContainer.hide();
                AJS.$(".quick-comment-body", $container).append("<div class=\"quick-comment-error-box\"></div>");
                AJS.messages.error(".quick-comment-error-box", {
                    title: "Error loading the comment editor",
                    body: Confluence.Templates.Comments.editorLoadErrorMessage({ message: message, fallbackUrl: fallbackUrl}),
                    closeable: false
                });
            };
            
            // If loading fails
            var errorTimeoutId = setTimeout(function() {
                cancelLoadingMessage();
                unblocker();
                displayError("Timeout while waiting for the editor resources to load.", fallbackUrl);
            }, AJS.Confluence.EditorLoader.loadingTimeout + 1000);

            var cancelTimeoutMessage = function() {
                clearTimeout(errorTimeoutId);
            };
                        
            var successCallback = function() {
                cancelLoadingMessage();
                cancelTimeoutMessage();
                
                /* 
                 * Shortcuts are added and removed as the editor is activated and deactivated so
                 * bind to this event so we keey the 'm' shortcut for ourselves. 
                 */
                AJS.bind("add-bindings.keyboardshortcuts", function() {
                    AJS.whenIType("m").moveToAndClick(".quick-comment-prompt");
                });
                
                AJS.$(".quick-comment-prompt", $container).hide();
                $loadingContainer.hide();
                AJS.$(".quick-comment-body", $container).addClass("comment-body");
                $form.fadeIn("fast", function() {                
                    $spacer.hide();
                    SaveBarBinder.bind(saveHandler, cancelHandler);
                    
                    var text = unblocker();
                    if (text && text.length) {
                        AJS.Rte.getEditor().setContent(text, {format : 'raw'});      
                        // ensure cursor is placed after any text that is added
                        var sel = AJS.Rte.getEditor().selection;
                        sel.select(AJS.Rte.getEditor().getBody());
                        sel.collapse(false);                        
                    }
                    
                    // Duplicated from EditorLoader but Firefox requires the RTE focus
                    // to be set after the RTE has become visible
                    AJS.Rte.getEditor().focus();                    
                });            
            };
            
            var errorCallback = function(message) {
                cancelLoadingMessage();
                cancelTimeoutMessage(); // we have more specific error. Don't show this one.
                unblocker();
                displayError(message, fallbackUrl);
            };
            
            AJS.Confluence.EditorLoader.activate($form, successCallback, errorCallback);
        },
        
        /**
         * Most templates require a commenter parameter. This function creates it.
         * 
         * @param $userLogoImg an img representing a user log to create commenter details from
         */
        createCommenterParam: function($userLogoImg) {
            var userLogoSrc = $userLogoImg.attr("src");
            var ctxPath = AJS.Meta.get("context-path");
            
            if (ctxPath && userLogoSrc.indexOf(ctxPath) == 0) {
                userLogoSrc = userLogoSrc.substr(ctxPath.length); // strip the context path
            }

            var userName = AJS.Meta.get("remote-user");
            if (userName != null && userName.length == 0) {
                userName = null;
            }
            
            var commenter = {
                "userName": userName,
                displayName: AJS.Meta.get("user-display-name"),
                profilePicture: {
                    isDefault: $userLogoImg.hasClass("defaultLogo"),
                    path: userLogoSrc
                }
            };
            
            return commenter;
        },
        
        /**
         * Create a save handler which is a function taking a single event that is to be called
         * when the save operation is activated on the editor.
         * 
         * @param successHandler the function to be called if save is successful. Takes a single argument which is the 
         * data returned from the save. 
         * @param errorHandler a function taking a single message parameter which is called if the save fails.
         * @return a function taking an event parameter which is suitable for use as a save handler for the editor
         */
        createSaveHandler: function(successHandler, errorHandler) {
            var quickComment = this;
            return function(event) {
                if (!Confluence.Editor.UI.toggleSavebarBusy(true)) {
                    AJS.log("QuickComment: subsequent save operation attempted but ignored.");
                    return;
                }
                
                if (AJS.Rte.Content.isEmpty()) {
                    AJS.Confluence.EditorNotification.notify("warning", "Comment text is empty. Cannot create empty comments.", 8);
                    return;                
                } 

                // TODO add better progress indication rather than just changing save button to "saving"

                // TODO Not required because we aren't dynamically adding comments yet
                // Learn about the users current profile picture from their avatar already rendered beside the editor
                // var commenter = quickComment.createCommenterParam(AJS.Confluence.EditorLoader.getCurrentContainer().closest(".quick-comment-container").find(".userLogo"));

                // Check for a parent comment. 
                var parentCommentId = 0;
                var $form = AJS.Confluence.EditorLoader.getCurrentContainer();
                if ($form.is("form")) {
                    var action = $form.attr("action");
                    var match = action.match(/parentId=(\d+)/);
                    if (match && match.length > 1) {
                        parentCommentId = parseInt(match[1]);
                    }                    
                }
                
                var $watchPage = AJS.$("#watchPage", $form);
                var watch = false;
                if ($watchPage.length) {
                    watch = $watchPage[0].checked;
                }

                // captcha
                var $captchaIdInput = AJS.$('input[name="captchaId"]', $form);
                var captchaId = null;
                if ($captchaIdInput.length) {
                    captchaId = $captchaIdInput.val();
                }

                var $captchaResponseInput = AJS.$('input[name="captchaResponse"]', $form);
                var captchaResponse = null;
                if ($captchaResponseInput.length) {
                    captchaResponse = $captchaResponseInput.val();
                }

                var captcha = {
                    id: captchaId,
                    response: captchaResponse
                };


                var changeCaptchaErrorHandler = function(message) {
                    errorHandler(message);
                    var $img = AJS.$("img.captcha-image", $form);
                    if ($img.length) {
                        // update captcha with new id
                        var captchaId = +Math.random();
                        $img[0].src = AJS.contextPath() + "/jcaptcha?id=" + captchaId;
                        $captchaIdInput.val(captchaId);
                        $captchaResponseInput.val("");
                    }
                };
                
                Confluence.Editor.CommentManager.saveComment(Confluence.Editor.getContentId(), parentCommentId, AJS.Rte.Content.getHtml(), 
                        watch, captcha, successHandler, changeCaptchaErrorHandler);
            };
        },     

        /*
         * Provide feedback to the user that their click has done something (has caused the editor to begin 
         * loading) and set up a timer to show a message if loading is taking too long.
         * 
         * @return a zero argument function that can be used to cancel the 'placeholder' timer. So
         * if activation completes quickly enough you can use this function to cancel the display of
         * the editor placeholder
         */
        loadingTransition: function($form, $loadingContainer, $spacer) {
            $spacer.show(); // show the space to ensure the page is long enough to us to scroll to where there editor will be.
            $form.hide();
            $loadingContainer.show();
            
            // if loading is too slow then we will switch to showing a message
            var loadingTimeoutId = setTimeout(function() {
                AJS.$(".quick-comment-loading-message", $loadingContainer).show();
            }, 400);        
                    
            return function() {
                clearTimeout(loadingTimeoutId);            
            };
        },
        
        saveCommentErrorHandler: function(message) {
            Confluence.Editor.UI.toggleSavebarBusy(false);
            
            // recognise some common error conditions
            var displayMessage = "Failed to save the comment:" + " " + message;

            if (message && message.search(/captcha/i) != -1) {
                displayMessage = "The typed word did not match the text in the picture.";
            } 
            
            AJS.Confluence.EditorNotification.notify("error", displayMessage, 30);
        }
    };
    
    /**
     * Functionality specific to top level commenting.
     */
    QuickComment.TopLevel = {
            
        /**
         * Called to handle the triggering of a top level comment editor.
         * This is expected to called in the context where 'this' is the activated element. 
         * 
         * @param e the event triggering the activation
         */            
        activateEventHandler: function(e) {
            QuickComment.Reply.disable();
            
            var $container = AJS.$(this).closest(".quick-comment-container");
            
            // TODO we don't have dynamic addition of comments yet so this isn't required
//            var saveCommentSuccessHandler = function(data) {
//                Confluence.Editor.UI.toggleSavebarBusy(false);
//                QuickComment.TopLevel.restorePlaceholder();
//            };

            var saveHandler = QuickComment.createSaveHandler(reloadPageSaveCommentHandler, QuickComment.saveCommentErrorHandler);

            var fallbackUrl = AJS.$("#add-comment-rte").attr("href");
            return QuickComment.activateEditor($container, saveHandler, cancelHandler, QuickComment.TopLevel.loadingTransition, fallbackUrl);            
        },     
        
        /**
         * Both cancelling the comment editor and submitting a comment will result in the editor being hidden and
         * the place holder being re-instated. This function hosts this shared functionality
         */
        restorePlaceholder: function() {
            var $container = AJS.Confluence.EditorLoader.getCurrentContainer().closest(".quick-comment-container");
            if ($container == null) {
                AJS.log("QuickComment.TopLevel: Cannot dismiss the Editor. No current editor was found.");
                return;
            }        
            
            var $spacer = AJS.$(".quick-comment-vertical-spacer", $container);
            $spacer.show();
            $spacer[0].scrollIntoView(); // in case the comment we are dismissing was so large that the top scrolled off the viewport
            
            AJS.Confluence.EditorLoader.deactivate();
            
            AJS.$(".quick-comment-body", $container).removeClass("comment-body");
            var $prompt = AJS.$(".quick-comment-prompt", $container);
            
            QuickComment.TopLevel.enable();
            QuickComment.Reply.enable();
            
            $prompt.fadeIn("fast", function() {
                $spacer.hide();
                $prompt.blur();
            });  
        },        
            
        enable: function() {
            var $prompts = AJS.$(".quick-comment-prompt");
            AJS.log("QuickComment.TopLevel: binding " + $prompts.length + " element(s) to the 'click' event.");
            $prompts.one("click", this.activateEventHandler);
            $prompts.each(function(i, element) {
                element.disabled = false;
            });
        },
        
        /**
         * Ensure all top level comment place holders are disable and remove all handlers. 
         */
        disable: function() {
            var $prompts = AJS.$(".quick-comment-prompt");
            AJS.log("QuickComment.TopLevel: " + $prompts.length + " elements(s) to be disabled.");
            $prompts.unbind();
            $prompts.each(function(i, element) {
                element.disabled = true;
            });
        },
        
        /**
         * Ensure the entirety of the comment editor will be in view. This relies on the
         * fact that the 'add comment' editor is always at the bottom of the page. If it sat
         * higher on the page this method would always result in the comment editor being
         * scrolled to the top of the page.
         */
        loadingTransition: function($form, $loadingContainer, $spacer) {
            var cancelLoadingMessage = QuickComment.loadingTransition($form, $loadingContainer, $spacer);
            $spacer[0].scrollIntoView();
            return cancelLoadingMessage;
        }
    };
    
    /**
     * Functionality specific to replying to comments.
     */
    QuickComment.Reply = {

        /**
         * Will display the editor structure when reply is activated. Expects 'this' to be the DOM element
         * that was activated.
         * 
         * @param e the event triggering the activation
         */
        activateEventHandler: function(e) {
            QuickComment.TopLevel.disable();
            QuickComment.Reply.disable();
            
            var $thread = AJS.$(this).closest(".comment-thread");
            var $comment = AJS.$(this).closest("div.comment");
            
            var $placeHolderLogo = AJS.$(".quick-comment-container img.userLogo");
            var commenter = QuickComment.createCommenterParam($placeHolderLogo);
            
            var match = $comment.attr("id").match(/comment-(\d+)/);
            var parentCommentId = 0;
            if (match) {
                parentCommentId = parseInt(match[1]);
            } else {
                AJS.log("QuickComment.Reply: activateEventHandler - could not extract a parent comment Id from the comment id " + $comment.attr("id"));
            }

            var params = {
                "contentId": Confluence.Editor.getContentId(),
                "parentCommentId": parentCommentId,
                "commenter": commenter,
                "context": {
                    "contextPath": AJS.Meta.get("context-path"),
                    "staticResourceUrlPrefix": AJS.Meta.get("static-resource-url-prefix")
                }
            };

            var template = Confluence.Templates.Comments.displayReplyEditorLoadingContainer(params);
            $comment.after(template);

            // Confluence.Editor.getCurrentForm requires this to match the name of the form
            // (which is important for the binding of the submit handler)
            var $container = AJS.$(".quick-comment-container", $thread);

            var formName = AJS.$("form", $container).attr("name");
            AJS.Meta.set("form-name", formName);
            
            /*
             * Commonality between cancelling or successfully saving a reply
             */
// TODO We don't dynamically add comments to the page yet so this isn't needed            
//            var cleanup = function($container) {
//                // Make sure we don't remove the editor placeholder
//                $container.remove();
//                QuickComment.TopLevel.enable();
//                QuickComment.Reply.enable();
//            };
//            
//            var saveCommentSuccessHandler = function(data) {
//                Confluence.Editor.UI.toggleSavebarBusy(false);
//                cleanup($container);
//            };
//            
//            var cancelCommentHandler = function() {
//                cleanup($container);
//            };
            
            var saveHandler = QuickComment.createSaveHandler(reloadPageSaveCommentHandler, QuickComment.saveCommentErrorHandler);

            var fallbackUrl = AJS.$("#reply-comment-" + parentCommentId).attr("href");
            
            QuickComment.activateEditor($container, saveHandler, cancelHandler, QuickComment.Reply.loadingTransition, fallbackUrl);            
            e.stopPropagation();
            return false;
        },
        
        enable: function() {
            var $replyLinks = AJS.$(".action-reply-comment");
            AJS.log("QuickComment.Reply: binding " + $replyLinks.length + " reply link(s) to the 'click' event.");
            $replyLinks.one("click", this.activateEventHandler);
            $replyLinks.each(function(i, element) {
                element.disabled = false;
            });
        },
        
        disable: function() {
            var $replyLinks = AJS.$(".action-reply-comment");
            AJS.log("QuickComment.Reply: unbinding " + $replyLinks.length + " reply link(s) from the 'click' event.");
            $replyLinks.unbind();
            $replyLinks.each(function(i, element) {
                element.disabled = true;
            });
        },
        
        /**
         * Ensure the entirety of the comment editor will be in view.
         * Only scroll the page if it is necessary to fit the comment editor in and attempt to 
         * scroll only enough to keep the editor in view.
         */
        loadingTransition: function($form, $loadingContainer, $spacer) {
            var cancelLoadingMessage = QuickComment.loadingTransition($form, $loadingContainer, $spacer);
            var $container = $loadingContainer.closest(".quick-comment-container");
            $container.scrollToElement();
            
            return cancelLoadingMessage;
        }
    };       
    
    // Customise the initialisation of AppLinks so that it won't start until the editor is initialised.
    AJS.AppLinksInitialisationBinder = function(f) {
        AJS.bind("init.rte", f);
    };    
  
    // Pre-load the editor (is there actually any point in having this delayed/in a different event loop?)
    setTimeout(function() {
        AJS.log("QuickComment: instigated background loading of the comment editor.");
        AJS.Confluence.EditorLoader.load();
        }, 1000);

    // A click event might cause load to happen before the loading timeout set earlier. 
    // That doesn't matter. EditorLoader protects against multiple loads.
    QuickComment.TopLevel.enable();
    QuickComment.Reply.enable();    
    
    // we override 'm' rather than change it in the keyboard-shortcuts plugin. This way if the quick-comment
    // plugin is disabled we still have the original behaviour.
    AJS.bind("initialize.keyboardshortcuts", function() {
        AJS.whenIType("m").moveToAndClick(".quick-comment-prompt");        
    });
});
} catch (err) {
    if (console && console.log) {
        console.log("Error loading resource (24326)");
        console.log(err);
    }
}

try {
AJS.bind("init.rte", function() {

    /**
     * A Manager that handles comment operations, as instigated by the Editor. This caveat basically means that
     * this manager deals with Editor formatted content. This Manager will deal with both handling the server side
     * operations involved in working with comments as well as also handling the displaying of comment operations by
     * delegating to the Confluence.CommentDisplayManager.
     */
    Confluence.Editor.CommentManager = (function($) {
        
        function getAddNewCommentUrl(contentId, actions) {
            var url = Confluence.getContextPath() + "/rest/tinymce/1/content/" + contentId + "/comment";
            if (actions) {
                url += "?actions=true";
            }
            
            return url;
        };
        
        function getReplyToCommentUrl(contentId, parentCommentId, actions) {
            var url = Confluence.getContextPath() + "/rest/tinymce/1/content/" + contentId + "/comments/" + parentCommentId + "/comment";
            if (actions) {
                url += "?actions=true";
            }
            
            return url;
        };
        
        return {
            
            /**
             * Save a new comment and display it if successful.
             * 
             * @param contentId the id of the content being commented on
             * @param parentId the of the comment being replied to. This should be 0 if this is not a reply
             * @param editorHtml editor formatted HTML which is the body of the comment
             * @param watch if true then start watching the content that is being commented on.
             * @param captcha the supplied captcha value (may be null if there is none supplied).
             * @param highlight true if you want the comment to appear highlighted
             * @param commenter the person making the comment. This is an object with the following structure:
             * {
             *     userName: (string),
             *     displayName: (string),
             *     profilePicture: {
             *         isDefault: (boolean),
             *         path: (string)
             *     }
             * }
             * @param successCallback a function taking a single parameter which represents the server returned comment that 
             * is called on success. The parameter has the structure:
             * {
             *    id: (number) the id of the comment
             *    html: (HTML string) the rendered content of the comment
             *    ownerId: (number) the id of the content commented upon
             *    parentId: (number) the id of the comment this one is in reply to
             * }
             * @param errorCallback a function taking a single parameters which describes the error returned
             */
            addComment: function(contentId, parentId, editorHtml, watch, captcha, highlight, commenter, successCallback, errorCallback) {
                var saveCommentSuccessHandler = function(data) {
                    Confluence.CommentDisplayManager.addComment(commenter, data, highlight);                
                    successCallback(data);
                };
                
                Confluence.Editor.CommentManager.saveComment(contentId, parentId, editorHtml, saveCommentSuccessHandler, errorCallback);                
            },
            
            /**
             * Save a new comment. If you also want to display the saved comment you should call addComment.
             * 
             * @param contentId the id of the content being commented on
             * @param parentId the of the comment being replied to. This should be 0 if this is not a reply
             * @param editorHtml editor formatted HTML which is the body of the comment
             * @param watch if true then start watching the content that is being commented on.
             * @param captcha the supplied captcha object
             * @param successCallback a function taking a single parameter which represents the server returned comment that 
             * is called on success. The parameter has the structure:
             * {
             *    id: (number) the id of the comment
             *    html: (HTML string) the rendered content of the comment
             *    ownerId: (number) the id of the content commented upon
             *    parentId: (number) the id of the comment this one is in reply to
             * }
             * @param errorCallback a function taking a single parameters which describes the error returned
             */            
            saveComment: function(contentId, parentId, editorHtml, watch, captcha, successCallback, errorCallback) {
                var url = null;
                if (parentId) {
                    url = getReplyToCommentUrl(contentId, parentId, true);
                } else {
                    url = getAddNewCommentUrl(contentId, true);
                }                
                
                var saveCommentSuccessHandler = function(data, textStatus, jqXHR) {
                    successCallback(data);
                };                
                
                var saveCommentErrorHandler = function(jqXHR, textStatus, errorThrown) {
                    var message = textStatus + ": " + errorThrown;
                    if (jqXHR.responseText) {
                        message = message + " - " + jqXHR.responseText;
                    }
                    errorCallback(message);
                };       
                
                var ajaxData = {
                    type: "POST",
                    url: url,
                    contentType: "application/x-www-form-urlencoded; charset=UTF-8",
                    data: {
                        "html": editorHtml,
                        "watch": watch
                    },
                    dataType : "json",
                    cache: false,
                    headers: {
                        "X-Atlassian-Token" : "nocheck"  
                    },
                    success : saveCommentSuccessHandler,
                    error : saveCommentErrorHandler,
                    timeout: 120000
                }; 
                
                if (captcha.id) {
                    ajaxData.headers["X-Atlassian-Captcha-Id"] = captcha.id;
                    ajaxData.headers["X-Atlassian-Captcha-Response"] = captcha.response;
                }
                
                AJS.$.ajax(ajaxData);  
            }
        };    
    })(AJS.$);
});
} catch (err) {
    if (console && console.log) {
        console.log("Error loading resource (12197)");
        console.log(err);
    }
}

