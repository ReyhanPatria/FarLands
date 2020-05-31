package net.farlands.odyssey.command.player;

import static net.farlands.odyssey.util.FLUtils.giveItem;

import com.kicas.rp.util.TextUtils;
import net.farlands.odyssey.FarLands;
import net.farlands.odyssey.command.PlayerCommand;
import net.farlands.odyssey.data.FLPlayerSession;
import net.farlands.odyssey.data.Rank;
import net.farlands.odyssey.util.Logging;
import net.farlands.odyssey.util.TimeInterval;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommandGuideBook extends PlayerCommand {
    private final ItemStack book;

    public CommandGuideBook() {
        super(Rank.INITIATE, "Get a guide book.", "/guidebook", "guidebook");
        this.book = buildBook();
    }

    @Override
    public boolean execute(Player sender, String[] args) {
        FLPlayerSession session = FarLands.getDataHandler().getSession(sender);

        // Check the cooldown
        long cooldownTime = session.commandCooldownTimeRemaining(this);
        if (cooldownTime > 0L) {
            TextUtils.sendFormatted(sender, "&(red)You can use this command again in %0.",
                    TimeInterval.formatTime(cooldownTime * 50L, false));
            return true;
        }

        session.setCommandCooldown(this, 60L * 20L);
        giveItem(sender, book.clone(), true);

        return true;
    }

    private static ItemStack buildBook() {
        ItemStack writtenBook = new ItemStack(Material.WRITTEN_BOOK);
        writtenBook.setItemMeta(writeBook(writtenBook));
        return writtenBook;
    }

    private static List<String> loadBook() {
        try {
            return Arrays.asList(FarLands.getDataHandler().getDataTextFile("guidebook.txt").split("\n"));
        }catch(IOException ex) {
            Logging.error("Failed to load guidebook file.");
            ex.printStackTrace(System.out);
        }
        return Collections.emptyList();
    }

    private static BookMeta writeBook(ItemStack writtenBook) {
        BookMeta bookMeta = (BookMeta) writtenBook.getItemMeta();
        bookMeta.setTitle("Guide Book");
        bookMeta.setAuthor("Farlands Staff");
        bookMeta.setPages(loadBook());
        return bookMeta;
    }
}
