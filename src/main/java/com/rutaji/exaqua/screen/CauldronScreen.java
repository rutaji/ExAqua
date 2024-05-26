package com.rutaji.exaqua.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.rutaji.exaqua.ExAqua;
import com.rutaji.exaqua.container.CauldronContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import org.jetbrains.annotations.NotNull;

/**
 * Screen for {@link com.rutaji.exaqua.block.CraftingCauldron Cauldron} UI.
 * This class handles graphic on clinets side. Logic is handled by {@link CauldronContainer CauldronContainer}.
 */
public class CauldronScreen extends ContainerScreen<CauldronContainer> {
    private final ResourceLocation GUI = new ResourceLocation(ExAqua.MOD_ID,"gui/cauldron.png");
    //region Constructor
    public CauldronScreen(CauldronContainer p_i51105_1_, PlayerInventory p_i51105_2_, ITextComponent p_i51105_3_) {
        super(p_i51105_1_, p_i51105_2_, p_i51105_3_);
    }
    //endregion

    @Override
    protected void drawGuiContainerBackgroundLayer(@NotNull MatrixStack matrixStack, float partialTicks, int x, int y) {
        RenderSystem.color4f(1f, 1f, 1f, 1f);
        this.minecraft.getTextureManager().bindTexture(GUI);
        int i = this.guiLeft;
        int j = this.guiTop;
        this.blit(matrixStack, i, j, 0, 0, this.xSize, this.ySize);
        switch (container.getTemp())
        {
            case hot: this.blit(matrixStack, i+76, j+55, 177, 1, 24, 19); break;
            case cold: this.blit(matrixStack, i+76, j+55, 177, 21, 24, 19); break;
            case neutral: this.blit(matrixStack, i+76, j+55, 177, 41, 24, 19); break;
        }
        drawLiquid(i,j,matrixStack);

    }


    private void drawLiquid(int i, int j, MatrixStack matrixStack){
        String liguid = container.getLiquid();
        Minecraft.getInstance().fontRenderer.drawString(matrixStack,liguid , i +87 - font.getStringWidth(liguid)/2 , j+10, 0x111111);
        String toDraw = container.getLiquidAmount() +" mB";
        if(!liguid.equals("Empty")) {Minecraft.getInstance().fontRenderer.drawString(matrixStack,toDraw, i +87 - font.getStringWidth(toDraw)/2 , j+20, 0x111111);}

    }
    @Override
    public void render(@NotNull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderHoveredTooltip(matrixStack, mouseX, mouseY);

    }
}

