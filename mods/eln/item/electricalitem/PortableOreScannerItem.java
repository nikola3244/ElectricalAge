package mods.eln.item.electricalitem;

import java.awt.Color;
import java.util.List;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.achievement.GuiAchievement;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.client.IItemRenderer.ItemRendererHelper;
import mods.eln.Eln;
import mods.eln.PlayerManager;
import mods.eln.generic.GenericItemUsingDamageDescriptor;
import mods.eln.item.electricalinterface.IItemEnergyBattery;
import mods.eln.misc.Obj3D;
import mods.eln.misc.Obj3D.Obj3DPart;
import mods.eln.misc.Utils;
import mods.eln.wiki.Data;

public class PortableOreScannerItem extends GenericItemUsingDamageDescriptor implements IItemEnergyBattery{


	

	


	public PortableOreScannerItem(
			String name,Obj3D obj,
			double energyStorage,double chargePower,double dischargePower,
			float viewRange,float viewYAlpha,
			int resWidth,int resHeight
			) {
		super(name);
		this.chargePower = chargePower;
		this.dischargePower = dischargePower;
		this.energyStorage = energyStorage;
		
		this.viewRange = viewRange;
		this.viewYAlpha = viewYAlpha;
		this.resWidth = resWidth;
		this.resHeight = resHeight;
		this.obj = obj;
		
		if(obj != null){
			base = obj.getPart("Base");
			led = obj.getPart("Led");
			ledHalo = obj.getPart("LedHalo");
		}
	}

	double energyStorage, dischargePower, chargePower;
	float viewRange, viewYAlpha;
	int resWidth, resHeight;
	private Obj3D obj;

	Obj3DPart base,led,ledHalo;
	
	
	
	@Override
	public void setParent(Item item, int damage) {
		// TODO Auto-generated method stub
		super.setParent(item, damage);
		Data.addPortable(newItemStack());
	}
	
	@Override
	public NBTTagCompound getDefaultNBT() {
		NBTTagCompound nbt = new NBTTagCompound("itemStackNBT");
		nbt.setDouble("energy",0);
		return nbt;
	}
	

	

	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer,
			List list, boolean par4) {
		// TODO Auto-generated method stub
		super.addInformation(itemStack, entityPlayer, list, par4);
		
		list.add(Utils.plotEnergy("Energy stored", getEnergy(itemStack)) + "(" + (int)(getEnergy(itemStack)/energyStorage*100) + "%)");
	}


	public double getEnergy(ItemStack stack)
	{
		return getNbt(stack).getDouble("energy");
	}
	public void setEnergy(ItemStack stack,double value)
	{
		getNbt(stack).setDouble("energy",value);
	}

	@Override
	public double getEnergyMax(ItemStack stack) {
		// TODO Auto-generated method stub
		return energyStorage;
	}

	@Override
	public double getChargePower(ItemStack stack) {
		// TODO Auto-generated method stub
		return chargePower;
	}

	@Override
	public double getDischagePower(ItemStack stack) {
		// TODO Auto-generated method stub
		return dischargePower;
	}

	@Override
	public int getPriority(ItemStack stack) {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item,
			ItemRendererHelper helper) {
		if(type == ItemRenderType.INVENTORY)
			return false;
		return true;
	}
	
	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {		
		if(type == ItemRenderType.INVENTORY)		
			Utils.drawEnergyBare(type,(float) (getEnergy(item)/getEnergyMax(item)));
		
		
		
		
		 
		Object oRender = Eln.clientLiveDataManager.getData(item, 1);
		if(oRender == null) oRender =  Eln.clientLiveDataManager.newData(item, new RenderStorage(viewRange,viewYAlpha,resWidth,resHeight), 1);
		RenderStorage render = (RenderStorage) oRender;
		
		Entity e;
		
		switch (type) {
		case ENTITY:
			e = null;//(Entity)data[1];
			break;
		case EQUIPPED:
			e = null;//(Entity)data[1];
			break;
		case EQUIPPED_FIRST_PERSON:
			e = (Entity)data[1];
			break;
		case INVENTORY:
			e = null;
			break;
		case FIRST_PERSON_MAP:
			e = null;//(Entity)data[0];
			break;
	
		default:
			e = null;
			break;
		}
		
		if(e != null){
			render.generate(e.worldObj, e.posX, e.posY, e.posZ, e.rotationYaw * (float)Math.PI / 180.0F, e.rotationPitch * (float)Math.PI / 180.0F);
		}
		
		
		GL11.glPushMatrix();
			GL11.glTranslatef(0, 1, 0);
			GL11.glRotatef(90, 0, 0, 1);
			GL11.glRotatef(40, 1, 0, 0);
			GL11.glTranslatef(0.0f, 1, -0.2f);
			base.draw();
		
			Utils.disableLight();
			led.draw();
			Utils.enableBlend();
			ledHalo.draw();
			Utils.disableBlend();
			Utils.enableLight();
			
			float scale = 1f/resWidth/2;
			float p = 1/64f;
			GL11.glTranslatef(0.9125f-p*1, 0.163f, -0.25625f+p*1.5f);
			GL11.glRotatef(270, 1,0, 0);
			GL11.glRotatef(270, 0,0, 1);
			//GL11.glTranslatef(0, 1, 0);
			GL11.glScalef(scale, -scale, 1);
			render.draw();
		GL11.glPopMatrix();
	}

	
	public static class RenderStorage{
		public RenderStorage(float viewRange,float viewYAlpha,
							int resWidth,int resHeight) {
			this.viewRange = viewRange;
			this.viewYAlpha = viewYAlpha;
			this.camDist = (float) (resWidth/2/Math.tan(viewYAlpha/2));
			this.resWidth = resWidth;
			this.resHeight = resHeight;
			this.worldBlocksDim = (int) (viewRange*2 + 3);
			this.worldBlocksDim2 = this.worldBlocksDim / 2 ;
			screenRed = new float[resHeight][resWidth];
			screenBlue = new float[resHeight][resWidth];
			screenGreen = new float[resHeight][resWidth];
			worldBlocks = new short[worldBlocksDim][worldBlocksDim][worldBlocksDim];
		}
		
		float camDist;
		float viewRange, viewYAlpha,viewXAlpha;
		int resWidth, resHeight;
		float[][] screenRed,screenBlue,screenGreen;
		short[][][] worldBlocks;
		int worldBlocksDim,worldBlocksDim2;
		
		static float[] blockKeyFactor;
		float[] getBlockKeyFactor(){
			if(blockKeyFactor == null){
				blockKeyFactor = new float[1024*64];
				for(int blockId = 0;blockId < 4096;blockId++){
					Block block = Block.blocksList[blockId];
					for(int meta = 0;meta < 16;meta++){
						//if(block == null)
							blockKeyFactor[blockId + (meta << 12)] = 0;
						//else
							//blockKeyFactor[blockId + (meta << 12)] = block.isOpaqueCube() ? 0.2f : 0;
					}
				}
			}
			
			for(OreScannerConfigElement c : Eln.instance.oreScannerConfig){
				blockKeyFactor[c.blockKey] = c.factor;
			}
			return blockKeyFactor;
		}
		
		
		public static class OreScannerConfigElement{
			public OreScannerConfigElement(int blockKey,float factor){
				this.blockKey = blockKey;
				this.factor = factor;
			}
			
			public int blockKey;
			public float factor;
		}
		
		void generate(	World w,double posX,double posY,double posZ,
						float alphaY,float alphaX){
			float[] blockKeyFactor = getBlockKeyFactor();
			long start = System.nanoTime();
			
			int posXint = (int)Math.round(posX);
			int posYint = (int)Math.round(posY);
			int posZint = (int)Math.round(posZ);			

			for(int z = 0;z < worldBlocksDim;z++){
				for(int y = 0;y < worldBlocksDim;y++){
					for(int x = 0;x < worldBlocksDim;x++){
						worldBlocks[x][y][z] = -1;
					}					
				}				
			}
			


			for(int screenY = 0;screenY < resHeight;screenY++){
				int i = 0;
				i++;
				for(int screenX = 0;screenX < resWidth;screenX++){
					float x = (float) (posX - posXint),y = (float) (posY - posYint),z = (float) (posZ - posZint);
					
		            float vx = -(screenX - resWidth / 2);
					float vy = -(screenY - resHeight / 2);
		            float vz = camDist;

		            {
			            float sin = MathHelper.sin(alphaX);
			            float cos = MathHelper.cos(alphaX);
			            
			            float temp = vy;
			            vy = vy * cos - vz * sin;
			            vz = vz * cos + temp * sin;
		            }
		            {
			            float sin = MathHelper.sin(alphaY);
			            float cos = MathHelper.cos(alphaY);
			            
			            float temp = vx;
			            vx = vx * cos - vz * sin;
			            vz = vz * cos + temp * sin;
		            }
		            

		            
		            float normInv = 1f/(float) Math.sqrt(vx*vx+vy*vy+vz*vz);
		            vx *= normInv;
		            vy *= normInv;
		            vz *= normInv;
					
					if(vx == 0) vx += 0.0001f;
					if(vy == 0) vy += 0.0001f;
					if(vz == 0) vz += 0.0001f;
					
					
					float vxInv = 1f/vx,vyInv = 1f/vy,vzInv = 1f/vz;
					

					
					float stackRed = 0,stackBlue = 0,stackGreen = 0;
					float d = 0;
					while(d < viewRange){
						float xFloor = MathHelper.floor_float(x);
						float yFloor = MathHelper.floor_float(y);
						float zFloor = MathHelper.floor_float(z);
						
						float dx = x-xFloor,dy = y-yFloor,dz = z-zFloor;
						dx = (vx > 0 ? (1 - dx)*vxInv : -dx*vxInv);
						dy = (vy > 0 ? (1 - dy)*vyInv : -dy*vyInv);
						dz = (vz > 0 ? (1 - dz)*vzInv : -dz*vzInv);
						
						float dBest =  Math.min(Math.min(dx, dy),dz)+0.01f;
						//float dBest = 0.5f;

						int xInt = (int)xFloor + worldBlocksDim2;
						int yInt = (int)yFloor + worldBlocksDim2;
						int zInt = (int)zFloor + worldBlocksDim2;

						//short blockKey = (short) w.getBlockId(posXint + MathHelper.floor_float(x), posYint + MathHelper.floor_float(y), posZint + MathHelper.floor_float(z));;
						int blockKey = worldBlocks[xInt][yInt][zInt];
						if(blockKey < 0) blockKey += 65536;
						if(blockKey == 65535){
							//short blockKey;
							///w.getBlockId(par1, par2, par3)
							int xBlock = posXint + (int)xFloor;
							int yBlock = posYint + (int)yFloor;
							int zBlock = posZint + (int)zFloor;
							blockKey = 0;
							if(yBlock >= 0 && yBlock < 256){
								Chunk chunk = w.getChunkFromBlockCoords(xBlock, zBlock);
								if(chunk != null){
									ExtendedBlockStorage storage = chunk.getBlockStorageArray()[yBlock>>4];
									if(storage != null){
										int xLocal = xBlock & 0xF;
										int yLocal = yBlock & 0xF;
										int zLocal = zBlock & 0xF;
										
										blockKey = (storage.getExtBlockID(xLocal, yLocal, zLocal) + (storage.getExtBlockMetadata(xLocal, yLocal, zLocal) << 12));
									}
								}
							}
							//blockKey = (short) w.getBlockId(posXint + MathHelper.floor_float(x), posYint + MathHelper.floor_float(y), posZint + MathHelper.floor_float(z));;
							worldBlocks[xInt][yInt][zInt] = (short) blockKey;
						}
						
						
						float dToStack;
						if(d + dBest < viewRange)
							dToStack = dBest;
						else{
							dToStack = (viewRange - d);
						}	
						
						
						
						//dToStack += ((float)Math.random()-0.5f) * 0.1f;
												
						/*if(blockKey != 0){
							stackRed += 0.2f*dToStack;			
							if(blockKey != Block.dirt.blockID && blockKey != Block.stone.blockID)
								stackGreen += 0.2f*dToStack;		
						}*/
						
						stackGreen += blockKeyFactor[blockKey]*dToStack;
						Block b = Block.blocksList[blockKey & 0xFFF];
						if(b != null && b.isOpaqueCube())
							stackRed += 0.2f*dToStack;	
						else
							stackBlue += 0.06f*dToStack;
						/*else
							stackRed += */



						x += vx*dBest;
						y += vy*dBest;
						z += vz*dBest;
						
						d += dBest;
					
					}
					
					
					screenRed[screenY][screenX] = stackRed-stackGreen*0f;
					screenGreen[screenY][screenX] = stackGreen;
					screenBlue[screenY][screenX] = stackBlue-stackGreen*0f;
				}				
			}
			long end = System.nanoTime();
			System.out.println("Generate : " + (end - start)/1000 + "us");
		}
		
		void draw(){
			long start = System.nanoTime();
			Utils.disableLight();
			Utils.disableTexture();
			//GL11.glShadeModel(GL11.GL_SMOOTH);

			for(int screenY = 0;screenY < resHeight-1;screenY++){
				GL11.glBegin(GL11.GL_QUAD_STRIP);
				for(int screenX = 0;screenX < resWidth-1;screenX++){
					float s;
					
					//s = screen[screenY][screenX]; GL11.glColor3f(s >= 0 ? s : 0, 0, s < 0.1 ? -s + 0.1f : 0);		
					//Color c = Color.getHSBColor(Math.max(0,Math.min(1,s)),1,1);
				//	GL11.glColor3ub((byte)c.getRed(),(byte)c.getGreen(),(byte)c.getBlue());		
					GL11.glColor3f(screenRed[screenY][screenX], screenGreen[screenY][screenX], screenBlue[screenY][screenX]);
					GL11.glVertex3f(screenX, screenY, 0);
					GL11.glVertex3f(screenX, screenY+1, 0);
					
					
					
					
					
					/*-GL11.glColor3f(screen[screenY][screenX], 0, 0);
					GL11.glVertex3f(screenX, screenY, 0);
					GL11.glColor3f(screen[screenY+1][screenX], 0, 0);
					GL11.glVertex3f(screenX, screenY+1, 0);
					GL11.glColor3f(screen[screenY+1][screenX+1], 0, 0);
					GL11.glVertex3i(screenX+1, screenY+1, 0);						
					GL11.glColor3f(screen[screenY][screenX], 0, 0);
					GL11.glVertex3f(screenX, screenY, 0);
					GL11.glColor3f(screen[screenY+1][screenX+1], 0, 0);
					GL11.glVertex3i(screenX+1, screenY+1, 0);					
					GL11.glColor3f(screen[screenY][screenX+1], 0, 0);
					GL11.glVertex3f(screenX+1, screenY, 0);-*

					/*GL11.glColor3f(screen[screenY+1][screenX+1], 0, 0);
					GL11.glVertex3i(screenX+1, screenY+1, 0);
					GL11.glColor3f(screen[screenY][screenX+1], 0, 0);
					GL11.glVertex3i(screenX+1, screenY, 0);*/
				}
				GL11.glEnd();
			}
			Utils.enableTexture();
			Utils.enableLight();
			//GL11.glShadeModel(GL11.GL_FLAT);
			long end = System.nanoTime();
			//System.out.println("Draw : " + (end - start)/1000 + "us");
		}	
	}
	
	
	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world,
			int x, int y, int z, int side, float vx, float vy, float vz) {
		//Utils.clientOpenGui(new GuiChat());
		/*Side s = FMLCommonHandler.instance().getEffectiveSide();
		if (s == s.SERVER){
			setEnergy(stack, energyStorage/3);
		}*/
		return false;
	}
}



/*

	  for(int z = (posZint - worldBlocksDim2)&~15;z < posZint + worldBlocksDim2;z+=16){
	for(int x = (posXint - worldBlocksDim2)&~15;x < posXint + worldBlocksDim2;x+=16){
		for(int y = (posYint - worldBlocksDim2)&~15;y < posYint + worldBlocksDim2;y+=16){
			for(int zSub = Math.max(z, posZint - worldBlocksDim2) & 15;zSub < (z + 16 posZint + worldBlocksDim2< ((posZint + worldBlocksDim2)&15);z+=16){
				for(int xSub = Math.max(z, posZint - worldBlocksDim2) & 15;x < posXint - worldBlocksDim2;x+=16){
					for(int ySub = Math.max(z, posZint - worldBlocksDim2) & 15;y < posYint - worldBlocksDim2;y+=16){
						
						
					}
				}
			}						
		}
	}
}	*/