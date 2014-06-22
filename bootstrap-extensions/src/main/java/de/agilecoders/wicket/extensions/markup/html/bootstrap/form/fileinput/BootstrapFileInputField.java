package de.agilecoders.wicket.extensions.markup.html.bootstrap.form.fileinput;

import static de.agilecoders.wicket.jquery.JQuery.$;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.template.PackageTextTemplate;

import de.agilecoders.wicket.jquery.JQuery;
import de.agilecoders.wicket.jquery.util.Strings2;

/**
 * Integration with <a href="https://github.com/kartik-v/bootstrap-fileinput">Bootstrap FileInput</a>
 */
class BootstrapFileInputField extends FileUploadField {

    private static final String AJAX_EVENT_NAME_SUFFIX = "_fileinput-upload-button-clicked";

    /**
     * The labels supported by Bootstrap FileInput
     */
    private static final String[] LABELS = new String[] { "browseLabel", "removeLabel", "uploadLabel", "msgLoading", "msgProgress", "msgSelected" };

    /**
     * When the <em>Upload</em> button is used it will submit the file input
     * with Ajax. For that it needs to use AjaxFormSubmitBehavior.
     * This form is needed to prevent the submit of all other form components
     */
    private Form<?> form;

    /**
     * A behavior that is needed to be able to upload the files
     * by using the <em>Upload</em> button with Ajax
     */
    private AjaxFormSubmitBehavior ajaxUploadBehavior;

    FileInputConfig config = new FileInputConfig();

    /**
     * Constructor
     *
     * @param id The component id
     */
    public BootstrapFileInputField(String id) {
        this(id, null);
    }

    /**
     * Constructor
     *
     * @param id The component id
     * @param model The model that will store the uploaded files
     */
    public BootstrapFileInputField(final String id, final IModel<List<FileUpload>> model) {
        super(id, model);
    }

    void setForm(Form<?> form) {
        this.form = form;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        String ajaxEventName = Strings2.getMarkupId(this) + AJAX_EVENT_NAME_SUFFIX;
        ajaxUploadBehavior = newAjaxFormSubmitBehavior(ajaxEventName);
        add(ajaxUploadBehavior);
    }

    /**
     * Creates the special Ajax behavior that is used to upload the file(s)
     * with Ajax by using the <em>Upload</em> button
     *
     * @param event The name of the JavaScript event that will trigger the Ajax upload
     * @return The Ajax behavior for the file upload
     */
    protected AjaxFormSubmitBehavior newAjaxFormSubmitBehavior(String event) {
        return new AjaxFormSubmitBehavior(form, event) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                target.add(getForm());
                BootstrapFileInputField.this.onSubmit(target);
            }
        };
    }

    /**
     * A callback method that is called when there is an error during
     * an Ajax file upload
     *
     * @param target The Ajax request handler
     */
    void onError(AjaxRequestTarget target) {
    }

    /**
     * A callback method that is called on successful file upload triggered
     * by the usage of the <em>Upload</em> button.
     *
     * @param target The Ajax request handler
     */
    void onSubmit(AjaxRequestTarget target) {
    }

    @Override
    public void renderHead(final IHeaderResponse response) {
        FileinputJsReference.INSTANCE.renderHead(response);

        JQuery fileinputJS = $(this).chain("fileinput", config);

        PackageTextTemplate tmpl = new PackageTextTemplate(BootstrapFileInputField.class, "res/fileinput.tmpl.js");
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("markupId", Strings2.getMarkupId(this));
        for (String label : LABELS) {
            variables.put(label, getString(label));
        }
        variables.put("eventName", ajaxUploadBehavior.getEvent());

        String js = tmpl.asString(variables);

        response.render(OnDomReadyHeaderItem.forScript(fileinputJS.get() + js));
    }
}
