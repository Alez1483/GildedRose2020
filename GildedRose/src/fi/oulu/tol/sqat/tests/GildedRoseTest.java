package fi.oulu.tol.sqat.tests;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Test;

import fi.oulu.tol.sqat.GildedRose;
import fi.oulu.tol.sqat.Item;

public class GildedRoseTest
{
	@Test
	public void sellInTest()
	{
		GildedRose inn = new GildedRose();
		inn.setItem(new Item("Elixir of the Mongoose", 20, 20));

		Item elixir = inn.getItems().get(0);

		for (int expectedSellIn = 20; expectedSellIn > -5; expectedSellIn--)
		{
			// assert sellIn has decreased by one each day
			assertEquals("Failed to decrease sellIn by one every day", expectedSellIn, elixir.getSellIn());
			inn.oneDay();
		}
	}

	@Test
	public void qualityTest()
	{
		GildedRose inn = new GildedRose();
		inn.setItem(new Item("Elixir of the Mongoose", 10, 19));

		Item elixir = inn.getItems().get(0);

		for (int expectedQuality = 19; expectedQuality >= 9; expectedQuality--)
		{
			// assert quality has decreased by one each day
			assertEquals("Failed to decrease quality by one every day", expectedQuality, elixir.getQuality());
			inn.oneDay();
		}
		// sellIn should be negative at this point
		assertEquals("Failed to decrease sellIn by one every day", -1, elixir.getSellIn());

		// quality should decrease twice as fast
		for (int expectedQuality = 7; expectedQuality >= 1; expectedQuality -= 2)
		{
			// assert quality has decreased by two each day
			assertEquals("Failed to decrease quality by two after sell by date passed", expectedQuality, elixir.getQuality());
			inn.oneDay();
		}

		// quality should not get below 0
		for (int i = 0; i < 5; i++)
		{
			assertEquals("Failed to keep quality non negative", 0, elixir.getQuality());
			inn.oneDay();
		}
	}

	@Test
	public void agedBrieTest()
	{
		GildedRose inn = new GildedRose();
		inn.setItem(new Item("Aged Brie", 10, 10));

		Item agedBrie = inn.getItems().get(0);

		for (int expectedQuality = 10; expectedQuality <= 50; expectedQuality++)
		{
			// assert quality has increased by one each day
			assertEquals("Failed to increase quality by one every day", expectedQuality, agedBrie.getQuality());
			inn.oneDay();
		}

		// should not increase over 50
		for (int i = 0; i < 5; i++)
		{
			assertEquals("Failed to keep quality less or equal to 50", 50, agedBrie.getQuality());
			inn.oneDay();
		}
	}

	@Test
	public void sulfurasTest()
	{
		GildedRose inn = new GildedRose();
		inn.setItem(new Item("Sulfuras, Hand of Ragnaros", -10, 80));

		Item sulfuras = inn.getItems().get(0);

		// nothing should change
		for (int i = 0; i < 5; i++)
		{
			assertEquals("Failed to keep quality unchanged", 80, sulfuras.getQuality());
			assertEquals("Failed to keep sellIn unchanged", -10, sulfuras.getSellIn());
			inn.oneDay();
		}
	}

	@Test
	public void backstagePassTest()
	{
		GildedRose inn = new GildedRose();
		inn.setItem(new Item("Backstage passes to a TAFKAL80ETC concert", 15, 20));

		Item backstagePass = inn.getItems().get(0);

		int expectedQuality = 20;
		// sellIn from 15 to 11
		for (int sellIn = 15; sellIn > 10; sellIn--, expectedQuality++)
		{
			assertEquals("Failed to increase quality by one every day", expectedQuality, backstagePass.getQuality());
			inn.oneDay();
		}
		assertEquals("Failed to decrease sellIn by one every day", 10, backstagePass.getSellIn());
		// sellIn from 10 to 6
		for (int sellIn = 10; sellIn > 5; sellIn--, expectedQuality += 2)
		{
			assertEquals("Failed to increase quality by two every day (sellIn <= 10)", expectedQuality, backstagePass.getQuality());
			inn.oneDay();
		}
		assertEquals("Failed to decrease sellIn by one every day", 5, backstagePass.getSellIn());
		// sellIn from 5 to 0
		for (int sellIn = 5; sellIn > 0; sellIn--, expectedQuality += 3)
		{
			assertEquals("Failed to increase quality by three every day (sellIn <= 5)", expectedQuality, backstagePass.getQuality());
			inn.oneDay();
		}
		assertEquals("Failed to decrease sellIn by one every day", 0, backstagePass.getSellIn());
		// backstage pass quality should be 0 after the concert
		for (int i = 0; i < 5; i++)
		{
			inn.oneDay();
			assertEquals("Failed to set quality to 0", 0, backstagePass.getQuality());
		}

		// test addition by 2 not exceeding 50
		backstagePass.setQuality(49);
		backstagePass.setSellIn(10);
		for (int i = 0; i < 4; i++)
		{
			inn.oneDay();
			assertEquals("Failed ot keep quality at most 50", 50, backstagePass.getQuality());
		}

		// test addition by 3 not exceeding 50
		backstagePass.setQuality(48);
		backstagePass.setSellIn(5);
		for (int i = 0; i < 4; i++)
		{
			inn.oneDay();
			assertEquals("Failed ot keep quality at most 50", 50, backstagePass.getQuality());
		}
	}

	@Test
	public void mainMethodTest()
	{
		// construct GildedRose object so that values from the static array can be read
		// after main is called
		GildedRose inn = new GildedRose();

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(outputStream);

		System.setOut(out);

		GildedRose.main(null);

		String outputString = outputStream.toString();
		assertTrue(outputString.contains("OMGHAI!")); // tests the output to the console

		out.close();

		final int[] expectedQualities = { 19, 1, 6, 80, 21, 5 };
		for (int index = 0; index < 6; index++)
		{
			assertEquals("Failed to update quality correctly", expectedQualities[index], inn.getItems().get(index).getQuality());
		}
	}

	@Test
	public void loopTests()
	{
		final String[] testItems = { 
				"+5 Dexterity Vest", 
				"Aged Brie", 
				"Elixir of the Mongoose", 
				"Sulfuras, Hand of Ragnaros", 
				"Backstage passes to a TAFKAL80ETC concert", 
				"Conjured Mana Cake" };
		// look up values for the items above, starting from sellIn 20 and quality 10
		final int[] expectedSellInAfterDay = { 19, 19, 19, 20, 19, 19 };
		final int[] expectedQualityAfterDay = { 9, 11, 9, 10, 11, 9 };

		final int MAX_ITERATIONS = 1000;

		for (int iterations = 0; iterations < MAX_ITERATIONS; iterations++)
		{
			GildedRose inn = new GildedRose();

			// add iterations amount of items to inn
			for (int itemIndex = 0; itemIndex < iterations; itemIndex++)
			{
				inn.setItem(new Item(testItems[itemIndex % testItems.length], 20, 10));
			}
			inn.oneDay();
			for (int itemIndex = 0; itemIndex < iterations; itemIndex++)
			{
				int lookupIndex = itemIndex % testItems.length;
				Item item = inn.getItems().get(itemIndex);
				assertEquals("Failed to update sell in properly for " + item.getName(), expectedSellInAfterDay[lookupIndex], item.sellIn);
				assertEquals("Failed to update quality properly for " + item.getName(), expectedQualityAfterDay[lookupIndex], item.quality);
			}
		}
	}
}
