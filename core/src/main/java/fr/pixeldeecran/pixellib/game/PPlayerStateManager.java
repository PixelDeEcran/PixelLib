package fr.pixeldeecran.pixellib.game;

import fr.pixeldeecran.pixellib.game.utils.GameCallback;
import fr.pixeldeecran.pixellib.game.utils.GameFunction;
import lombok.Data;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;

@Data
public class PPlayerStateManager<G extends PGame<?, ?>> implements Listener {

    private final boolean fullReset;
    private final GameMode gameMode;
    private final List<GameCallback<G, Player>> whenSetupPlayer;
    private final List<GameCallback<G, Player>> whenStateJoined;
    private final List<GameCallback<G, Player>> whenStateLeft;
    private final List<GameCallback<G, Player>> whenQuit;
    private final List<GameCallback<G, Player>> whenRejoin;
    private final List<GameFunction<G, BlockBreakEvent, Boolean>> canBreakBlock;
    private final List<GameFunction<G, BlockPlaceEvent, Boolean>> canPlaceBlock;
    private final List<GameFunction<G, PlayerDropItemEvent, Boolean>> canDropItem;
    private final boolean canBeDamaged;
    private final boolean canLostFood;
    private final boolean canMoveItem;
    private final boolean canDragItem;
    private final List<GameCallback<G, PlayerInteractEvent>> whenInteract;
    private final List<GameCallback<G, EntityDamageEvent>> whenDie;

    private IEnumPlayerState<G> state;
    private G game;

    public void onStateJoined(Player player) {
        this.setupPlayer(player);

        this.whenStateJoined.forEach(callback -> callback.run(this.game, player));
    }

    public void onStateLeft(Player player) {
        this.whenStateLeft.forEach(callback -> callback.run(this.game, player));
    }

    public void onQuit(Player player) {
        this.whenQuit.forEach(callback -> callback.run(this.game, player));
    }

    public void onRejoin(Player player) {
        this.setupPlayer(player);

        this.whenRejoin.forEach(callback -> callback.run(this.game, player));
    }

    public void setupPlayer(Player player) {
        if (this.fullReset) {
            player.setWalkSpeed(0.2F);
            player.setFlySpeed(0.1F);
            player.setMaxHealth(20.0D);
            player.setHealth(20.0D);
            player.setFoodLevel(20);
            player.setLevel(0);
            player.setExp(0.0F);
            for (PotionEffect effect : player.getActivePotionEffects()) {
                player.removePotionEffect(effect.getType());
            }

            player.getInventory().clear();
            player.getInventory().setHelmet(new ItemStack(Material.AIR));
            player.getInventory().setChestplate(new ItemStack(Material.AIR));
            player.getInventory().setLeggings(new ItemStack(Material.AIR));
            player.getInventory().setBoots(new ItemStack(Material.AIR));
            player.updateInventory();
        }

        player.setGameMode(this.gameMode);

        this.whenSetupPlayer.forEach(callback -> callback.run(this.game, player));
    }



    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        if (this.hasState(player)) {
            if (this.canBreakBlock.size() > 0) {
                if (this.canBreakBlock.stream().noneMatch(function -> function.apply(this.game, event))) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        if (this.hasState(player)) {
            if (this.canPlaceBlock.size() > 0) {
                if (this.canPlaceBlock.stream().noneMatch(function -> function.apply(this.game, event))) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        if (this.hasState(player)) {
            if (this.canDropItem.size() > 0) {
                if (this.canDropItem.stream().noneMatch(function -> function.apply(this.game, event))) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (this.hasState(player)) {
                if (!this.canBeDamaged) {
                    event.setCancelled(true);
                } else {
                    if (player.getHealth() - event.getFinalDamage() <= 0) {
                        this.whenDie.forEach(callback -> callback.run(this.game, event));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (this.hasState(player)) {
                if (!this.canLostFood) {
                    event.setFoodLevel(20);
                }
            }
        }
    }

    @EventHandler
    public void onInventoryMoveItem(InventoryMoveItemEvent event) {
        if (event.getSource().getHolder() instanceof Player) {
            Player player = (Player) event.getSource().getHolder();

            if (this.hasState(player)) {
                if (!this.canMoveItem) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();

            if (this.hasState(player)) {
                if (!this.canDragItem) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (this.hasState(player)) {
            this.whenInteract.forEach(callback -> callback.run(this.game, event));
        }
    }

    public void setup(PGame<?, ?> game, IEnumPlayerState<?> state) {
        this.game = (G) game;
        this.state = (IEnumPlayerState<G>) state;
    }

    public boolean hasState(Player player) {
        return this.game.getPlayers().containsKey(player) && this.game.getPlayerState(player) == this.state;
    }

    public static class Builder<G extends PGame<?, ?>> {

        private boolean fullReset;
        private GameMode gameMode;
        private final List<GameCallback<G, Player>> whenSetupPlayer;
        private final List<GameCallback<G, Player>> whenStateJoined;
        private final List<GameCallback<G, Player>> whenStateLeft;
        private final List<GameCallback<G, Player>> whenQuit;
        private final List<GameCallback<G, Player>> whenRejoin;
        private final List<GameFunction<G, BlockBreakEvent, Boolean>> canBreakBlock;
        private final List<GameFunction<G, BlockPlaceEvent, Boolean>> canPlaceBlock;
        private final List<GameFunction<G, PlayerDropItemEvent, Boolean>> canDropItem;
        private boolean canBeDamaged;
        private boolean canLostFood;
        private boolean canMoveItem;
        private boolean canDragItem;
        private final List<GameCallback<G, PlayerInteractEvent>> whenInteract;
        private final List<GameCallback<G, EntityDamageEvent>> whenDie;

        public Builder() {
            this.fullReset = false;
            this.gameMode = GameMode.SURVIVAL;
            this.whenSetupPlayer = new ArrayList<>();
            this.whenStateJoined = new ArrayList<>();
            this.whenStateLeft = new ArrayList<>();
            this.whenQuit = new ArrayList<>();
            this.whenRejoin = new ArrayList<>();
            this.canBreakBlock = new ArrayList<>();
            this.canPlaceBlock = new ArrayList<>();
            this.canDropItem = new ArrayList<>();
            this.canBeDamaged = true;
            this.canLostFood = true;
            this.canMoveItem = true;
            this.canDragItem = true;
            this.whenInteract = new ArrayList<>();
            this.whenDie = new ArrayList<>();
        }

        public Builder<G> fullReset() {
            this.fullReset = true;
            return this;
        }

        public Builder<G> gameMode(GameMode gameMode) {
            this.gameMode = gameMode;
            return this;
        }

        public Builder<G> whenSetupPlayer(GameCallback<G, Player> callback) {
            this.whenSetupPlayer.add(callback);
            return this;
        }

        public Builder<G> whenStateJoined(GameCallback<G, Player> callback) {
            this.whenStateJoined.add(callback);
            return this;
        }

        public Builder<G> whenStateLeft(GameCallback<G, Player> callback) {
            this.whenStateLeft.add(callback);
            return this;
        }

        public Builder<G> whenQuit(GameCallback<G, Player> callback) {
            this.whenQuit.add(callback);
            return this;
        }

        public Builder<G> whenRejoin(GameCallback<G, Player> callback) {
            this.whenRejoin.add(callback);
            return this;
        }

        public Builder<G> canBreakBlock(GameFunction<G, BlockBreakEvent, Boolean> callback) {
            this.canBreakBlock.add(callback);
            return this;
        }

        public Builder<G> cannotBreakBlock() {
            return this.canBreakBlock((game, event) -> false);
        }

        public Builder<G> canPlaceBlock(GameFunction<G, BlockPlaceEvent, Boolean> callback) {
            this.canPlaceBlock.add(callback);
            return this;
        }

        public Builder<G> cannotPlaceBlock() {
            return this.canPlaceBlock((game, event) -> false);
        }

        public Builder<G> canDropItem(GameFunction<G, PlayerDropItemEvent, Boolean> callback) {
            this.canDropItem.add(callback);
            return this;
        }

        public Builder<G> cannotDropItem() {
            return this.canDropItem((game, event) -> false);
        }

        public Builder<G> cannotBeDamaged() {
            this.canBeDamaged = false;
            return this;
        }

        public Builder<G> cannotLostFood() {
            this.canLostFood = false;
            return this;
        }

        public Builder<G> cannotMoveItem() {
            this.canMoveItem = false;
            return this;
        }

        public Builder<G> cannotDragItem() {
            this.canDragItem = false;
            return this;
        }

        public Builder<G> whenInteract(GameCallback<G, PlayerInteractEvent> callback) {
            this.whenInteract.add(callback);
            return this;
        }

        public Builder<G> whenDie(GameCallback<G, EntityDamageEvent> callback) {
            this.whenDie.add(callback);
            return this;
        }

        public PPlayerStateManager<G> build() {
            return new PPlayerStateManager<>(
                fullReset, gameMode, whenSetupPlayer, whenStateJoined, whenStateLeft, whenQuit, whenRejoin, canBreakBlock,
                canPlaceBlock, canDropItem, canBeDamaged, canLostFood, canMoveItem, canDragItem, whenInteract, whenDie
            );
        }
    }
}
