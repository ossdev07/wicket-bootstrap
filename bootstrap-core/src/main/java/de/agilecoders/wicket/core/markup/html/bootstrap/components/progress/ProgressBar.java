package de.agilecoders.wicket.core.markup.html.bootstrap.components.progress;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.ICssClassNameProvider;
import de.agilecoders.wicket.core.util.Attributes;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.lang.Args;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A component for <a href="http://getbootstrap.com/components/#progress">Progress bars</a>
 */
public class ProgressBar extends GenericPanel<Integer> {
    private static final Logger LOG = LoggerFactory.getLogger(ProgressBar.class);

    public static final int MIN = 0;
    public static final int MAX = 100;

    public enum Type implements ICssClassNameProvider {
        DEFAULT, INFO, SUCCESS, WARNING, DANGER;

        public String cssClassName() {
            return equals(DEFAULT) ? "" : "progress-bar-" + name().toLowerCase();
        }
    }

    private final RepeatingView stacks;

    /**
     * A flag indicating whether the progress bar is animated/active.
     */
    private boolean active = false;

    /**
     * A flag indicating whether the progress bar is striped.
     */
    private boolean striped = false;

    /**
     * Constructor.
     *
     * Creates an empty progress bar.
     * Use {@linkplain #addStacks(Stack...)}
     * to add stacks to it
     *
     * @param id The component id
     * @see #ProgressBar(String, org.apache.wicket.model.IModel, de.agilecoders.wicket.core.markup.html.bootstrap.components.progress.ProgressBar.Type, boolean)
     * @see #addStacks(Stack...)
     */
    public ProgressBar(String id) {
        this(id, null);
    }

    /**
     * Constructor.
     *
     * Creates a progress bar with a stack that uses the given model, default type and is not labeled.
     *
     * @param id The component id
     * @param model The model that will be used for the default stack.
     * @see #ProgressBar(String)
     */
    public ProgressBar(String id, IModel<Integer> model) {
        this(id, model, Type.DEFAULT, false);
    }

    /**
     * Constructor.
     *
     * Creates a progress bar with a stack that uses the given model, type and is not labeled.
     *
     * @param id The component id
     * @param model The model that will be used for the default stack.
     * @param type The type of the stack
     * @see #ProgressBar(String)
     */
    public ProgressBar(String id, IModel<Integer> model, Type type) {
        this(id, model, type, false);
    }

    /**
     * Constructor.
     *
     * Creates a progress bar with a stack that uses the given model, default type and it is labeled or not.
     *
     * @param id The component id
     * @param model The model that will be used for the default stack.
     * @param labeled A flag whether the stack should be labeled or not
     * @see #ProgressBar(String)
     */
    public ProgressBar(String id, IModel<Integer> model, boolean labeled) {
        this(id, model, Type.DEFAULT, labeled);
    }

    /**
     * Constructor.
     *
     * Creates a progress bar with a stack that uses the given model, type and is labeled or not.
     *
     * @param id The component id
     * @param model The model that will be used for the default stack.
     * @param type The type of the stack
     * @param labeled A flag whether the stack should be labeled or not
     * @see #ProgressBar(String)
     */
    public ProgressBar(String id, IModel<Integer> model, Type type, boolean labeled) {
        super(id, model);

        Args.notNull(type, "type");

        stacks = new RepeatingView("stacks");
        add(stacks);

        if (model != null) {
            Stack defaultStack = new Stack(getStackId(), () -> ProgressBar.this.getModelObject());
            defaultStack.type(type).labeled(labeled);
            addStacks(defaultStack);
        }
    }

    /**
     * Generates a safe component id for a stack in this progress bar
     * @return a component id for a stack
     */
    public String getStackId() {
        return stacks.newChildId();
    }

    /**
     * Adds stacks to this progress bar.
     *
     * @param _stacks The stacks to add
     * @return this instance, for method chaining
     */
    public ProgressBar addStacks(Stack... _stacks) {
        Args.notNull(_stacks, "_stacks");

        for (Stack stack : _stacks) {
            stacks.add(stack);
        }

        return this;
    }

    public boolean striped() {
        return striped;
    }

    public ProgressBar striped(boolean value) {
        striped = value;
        return this;
    }

    public boolean active() {
        return active;
    }

    public ProgressBar active(boolean value) {
        active = value;
        if (value) {
            striped(true);
        }
        return this;
    }

    /**
     * Returns whether the progress bar is complete or not.
     *
     * Useful only when used with the default stack, i.e. only when a constructor different than {@linkplain #ProgressBar(String)}
     * is used!
     *
     * @return {@code true} if the progress bar is complete.
     */
    public final boolean complete() {
        return value() == MAX;
    }

    /**
     * Sets a new value for the progress.
     *
     * Useful only when used with the default stack, i.e. only when a constructor different than {@linkplain #ProgressBar(String)}
     * is used!
     *
     * @return this instance, for method chaining.
     */
    public ProgressBar value(IModel<Integer> value) {
        setDefaultModel(value);
        return this;
    }

    /**
     * Sets a new value for the progress.
     *
     * Useful only when used with the default stack, i.e. only when a constructor different than {@linkplain #ProgressBar(String)}
     * is used!
     *
     * @return this instance, for method chaining.
     */
    public ProgressBar value(Integer value) {
        if (value < MIN) {
            LOG.warn("The provided value '{}' is smaller than the allowed minimum '{}'.", value, MIN);
            value = MIN;
        } else if (value > MAX) {
            LOG.warn("The provided value '{}' is bigger than the allowed maximum'{}'.", value, MAX);
            value = MAX;
        }
        setDefaultModelObject(value);
        return this;
    }

    /**
     * Returns the current value of the progress.
     *
     * Useful only when used with the default stack, i.e. only when a constructor different than {@linkplain #ProgressBar(String)}
     * is used!
     *
     * @return the current value of the progress.
     */
    public Integer value() {
        Integer value = getModelObject();
        if (value < MIN) {
            LOG.warn("The model object '{}' is smaller than the allowed minimum '{}'.", value, MIN);
            value = MIN;
        } else if (value > MAX) {
            LOG.warn("The model object '{}' is bigger than the allowed maximum'{}'.", value, MAX);
            value = MAX;
        }
        return value;
    }

    @Override
    protected void onComponentTag(ComponentTag tag) {
        super.onComponentTag(tag);

        internalOnComponentTag(tag, active(), striped());
    }

    /**
     * Checks the tag name and sets the Bootstrap CSS classes
     *
     * @param tag The component tag to check and manage
     */
    static void internalOnComponentTag(ComponentTag tag, boolean isActive, boolean isStriped) {
        if (!"div".equalsIgnoreCase(tag.getName())) {
            LOG.warn("You've added a progress bar component to a non 'div' tag: {}. Changing it to 'div'!",
                     tag.getName());

            tag.setName("div");
        }

        Attributes.addClass(tag, "progress");

        if (isActive) {
            Attributes.addClass(tag, "active");
        }

        if (isStriped) {
            Attributes.addClass(tag, "progress-striped");
        }
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        response.render(JavaScriptHeaderItem.forReference(new UploadProgressBarJavaScriptReference()));
    }

}
