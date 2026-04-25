package com.walhay.gregifiedenergistics.common.mui;

import com.cleanroommc.modularui.api.IPanelHandler;
import com.cleanroommc.modularui.api.drawable.IDrawable.DrawableWidget;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.Interactable;
import com.cleanroommc.modularui.drawable.ItemDrawable;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.widget.Widget;
import com.cleanroommc.modularui.widgets.layout.Grid;
import gregtech.api.unification.OreDictUnifier;
import gregtech.api.util.GTLog;
import java.util.List;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SubstitutionSlotWidget extends Widget<SubstitutionSlotWidget> implements Interactable {

	private List<ItemStack> items;
	private String oreDict;

	private IPanelHandler selectorPanel() {
		return IPanelHandler.simple(getPanel(), (parentPanel, player) -> buildUI(), true);
	}

	private ModularPanel buildUI() {
		int sq = (int) Math.sqrt(items.size());

		var panel = ModularPanel.defaultPanel("selector_panel_" + oreDict, 18 * sq + 12, 18 * sq + 12);

		var grid = new Grid()
				.pos(6, 6)
				.height(sq * 18)
				.minElementMargin(0)
				.minColWidth(18)
				.minRowHeight(18)
				.mapTo(sq, items.size(), index -> {
					var widget = new DrawableWidget(new ItemDrawable(items.get(index)));
					widget.onUpdateListener(this::listener);
					return widget;
				});

		panel.child(IKey.lang(oreDict).asWidget());
		panel.child(grid);

		return panel;
	}

	private void listener(DrawableWidget widget) {
		GTLog.logger.info("Hello world");
	}

	@Override
	public @NotNull Result onMousePressed(int mouseButton) {
		selectorPanel().togglePanel();
		return Interactable.super.onMousePressed(mouseButton);
	}

	public SubstitutionSlotWidget oreDict(String oreDict) {
		this.oreDict = oreDict;
		this.items = OreDictUnifier.getAllWithOreDictionaryName(oreDict);
		return this;
	}
}
