package com.rutaji.exaqua.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.rutaji.exaqua.ExAqua;
import com.rutaji.exaqua.container.CauldronContainer;
import com.rutaji.exaqua.container.SqueezerContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class CauldronScreen extends ContainerScreen<CauldronContainer> {
    private final ResourceLocation GUI = new ResourceLocation(ExAqua.MOD_ID,"gui/cauldron.png");
    //region Constructor
    public CauldronScreen(CauldronContainer p_i51105_1_, PlayerInventory p_i51105_2_, ITextComponent p_i51105_3_) {
        super(p_i51105_1_, p_i51105_2_, p_i51105_3_);
    }
    //endregion

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
        RenderSystem.color4f(1f, 1f, 1f, 1f);
        this.minecraft.getTextureManager().bindTexture(GUI);
        int i = this.guiLeft;
        int j = this.guiTop;
        this.blit(matrixStack, i, j, 0, 0, this.xSize, this.ySize);
        switch (container.GetTemp())
        {
            case hot: this.blit(matrixStack, i+76, j+55, 177, 1, 24, 19); break;
            case cold: this.blit(matrixStack, i+76, j+55, 177, 21, 24, 19); break;
            case neutral: this.blit(matrixStack, i+76, j+55, 177, 41, 24, 19); break;
        }
        DrawLiquid(i,j,matrixStack);

    }


    private void DrawLiquid(int i,int j,MatrixStack matrixStack){
        String liguid = container.GetLiquid();
        drawCenteredString(matrixStack,font, liguid, i +87, j+7,0);
        if(!liguid.equals("Empty")) {drawCenteredString(matrixStack,font,String.valueOf(container.GetLiquidAmount()) +" mB" , i +87, j+17,0);}

    }
    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderHoveredTooltip(matrixStack, mouseX, mouseY);

    }
}

