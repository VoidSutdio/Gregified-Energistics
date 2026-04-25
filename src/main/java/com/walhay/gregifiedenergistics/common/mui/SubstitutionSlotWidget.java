package com.walhay.gregifiedenergistics.common.mui;

import com.cleanroommc.modularui.api.IPanelHandler;
import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.Interactable;
import com.cleanroommc.modularui.drawable.ItemDrawable;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.value.sync.SyncHandler;
import com.cleanroommc.modularui.widget.Widget;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.layout.Grid;
import com.walhay.gregifiedenergistics.api.patterns.ISubstitutionStorage;
import gregtech.api.mui.GTGuiTextures;
import gregtech.api.unification.OreDictUnifier;
import java.io.IOException;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import org.jetbrains.annotations.NotNull;

public class SubstitutionSlotWidget extends Widget<SubstitutionSlotWidget> implements Interactable {

	private List<ItemStack> items;
	private SubstituionSyncHandler syncHandler;
	private String name;

	public SubstitutionSlotWidget() {
		size(18);
		background(GTGuiTextures.SLOT);
		var selected = new ItemDrawable();
		overlay(selected);
		onUpdateListener(w -> selected.setItem(items.get(syncHandler.getOption())));
	}

	public SubstitutionSlotWidget storage(ISubstitutionStorage storage, String name) {
		this.name = name;
		this.items = OreDictUnifier.getAllWithOreDictionaryName(name);
		this.syncHandler = new SubstituionSyncHandler(storage, name);
		setSyncOrValue(syncHandler);

		return this;
	}

	@Override
	public boolean isValidSyncHandler(SyncHandler syncHandler) {
		return syncHandler instanceof SubstituionSyncHandler;
	}

	@Override
	public SubstituionSyncHandler getSyncHandler() {
		return syncHandler;
	}

	private IPanelHandler selectorPanel() {
		return IPanelHandler.simple(getPanel(), (parentPanel, player) -> buildUI(), true);
	}

	private ModularPanel buildUI() {
		int sq = (int) Math.sqrt(items.size());

		var panel = ModularPanel.defaultPanel("selector_panel_" + name, 18 * sq + 12, 18 * sq + 12);

		var grid = new Grid()
				.pos(6, 6)
				.height(sq * 18)
				.minElementMargin(0)
				.minColWidth(18)
				.minRowHeight(18)
				.mapTo(sq, items.size(), index -> {
					var widget = new ButtonWidget<>()
							.background(IDrawable.of(GTGuiTextures.SLOT, new ItemDrawable(items.get(index))))
							.onMousePressed(button -> this.syncHandler.setSubstitution(index));
					return widget;
				});

		panel.child(IKey.lang(name).asWidget());
		panel.child(grid);

		return panel;
	}

	@Override
	public @NotNull Result onMousePressed(int mouseButton) {
		selectorPanel().togglePanel();
		return Interactable.super.onMousePressed(mouseButton);
	}

	public class SubstituionSyncHandler extends SyncHandler {

		private final ISubstitutionStorage storage;
		private final String name;
		public static final int SET_SUBSTITUTION = 0;

		public SubstituionSyncHandler(ISubstitutionStorage storage, String name) {
			this.storage = storage;
			this.name = name;
		}

		@Override
		public void readOnClient(int id, PacketBuffer buf) throws IOException {
			if (id == SET_SUBSTITUTION) {
				setSubstitution(buf.readVarInt());
			}
		}

		@Override
		public void readOnServer(int id, PacketBuffer buf) throws IOException {
			if (id == SET_SUBSTITUTION) {
				setSubstitution(buf.readVarInt());
			}
		}

		public boolean setSubstitution(int option) {
			if (storage != null && getOption() != option) {
				storage.setOption(name, option);
				sync(SET_SUBSTITUTION, buf -> buf.writeVarInt(option));
				return true;
			}
			return false;
		}

		public int getOption() {
			return storage.getOption(name);
		}
	}
}
