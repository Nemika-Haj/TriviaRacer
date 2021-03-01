package dracer.commands.user;

import dracer.commands.Command;
import dracer.racing.RaceHandler;
import dracer.racing.tasks.Task;
import dracer.util.CommandHandler;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class StartRace implements Command {
    public static final Logger lgr = LoggerFactory.getLogger(StartRace.class);
    private final GuildMessageReceivedEvent event;
    private final List<String> arguments;

    public StartRace(GuildMessageReceivedEvent event, List<String> arguments) {
        arguments.remove(0); // remove the command start.
        this.arguments = arguments;
        this.event = event;
        CommandHandler.queueCommand(this);
    }

    @Override
    public void run() {
        Task.TaskCategory category = Task.TaskCategory.ALL_CATEGORIES;

        if (!arguments.isEmpty()) {
            int categoryId;
            try {
                categoryId = Integer.parseInt(arguments.get(0));
                if (categoryId < 8 || categoryId > 32) {
                    throw new IllegalArgumentException("Category not found.");
                }
            } catch (Exception ex) {
                event.getChannel().sendMessage("Please insert a valid category ID! Look them up using `tcr.help`").queue();
                return;
            }
            for (Task.TaskCategory taskCategory : Task.TaskCategory.class.getEnumConstants()) {
                if (taskCategory.ordinal() == categoryId) {
                    category = taskCategory;
                    break;
                }
            }
        }

        if (!RaceHandler.isRaceActive(event.getChannel().getId())) {
            RaceHandler.addRace(category, event.getChannel(), event.getMember());
        } else {
            // noinspection ConstantConditions
            event.getChannel().sendMessage("<@" + event.getMember().getId() + "> A race is already running in this channel!").queue();
        }
    }

    @Override
    public Logger getLogger() {
        return lgr;
    }

    @Override
    public GuildMessageReceivedEvent getEvent() {
        return event;
    }
}
