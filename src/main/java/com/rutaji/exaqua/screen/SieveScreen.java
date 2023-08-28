package com.rutaji.exaqua.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.rutaji.exaqua.ExAqua;
import com.rutaji.exaqua.container.SieveContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class SieveScreen extends ContainerScreen<SieveContainer> {
    private final ResourceLocation GUI = new ResourceLocation(ExAqua.MOD_ID,"gui/sieve.png");
    //region Constructor
    public SieveScreen(SieveContainer p_i51105_1_, PlayerInventory p_i51105_2_, ITextComponent p_i51105_3_) {
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


        DrawLiquid(i,j,matrixStack);
        drawRF(i,j,matrixStack);

    }
    private void DrawLiquid(int i,int j,MatrixStack matrixStack){
        String liguid = container.GetLiquid();
        drawCenteredString(matrixStack,font, liguid, i +87, j+10,0);
        if(!liguid.equals("Empty")) {drawCenteredString(matrixStack,font,String.valueOf(container.GetLiquidAmount()) +" mB" , i +87, j+20,0);}

    }
    private void drawRF(int i,int j,MatrixStack matrixStack){
        drawCenteredString(matrixStack,font,container.GetEnergyAmount()  + " RF",i+24,j+40,0);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderHoveredTooltip(matrixStack, mouseX, mouseY);

    }
}
