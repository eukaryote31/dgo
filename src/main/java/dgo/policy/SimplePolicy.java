package dgo.policy;

import dgo.model.Goban;
import dgo.model.GobanValuesD;

public class SimplePolicy implements PolicyComponent {
	// A precomputed map of importance on the board. Also happens to encourage
	// tenuki by increasing the scores of unoccupied corners.
	private static final GobanValuesD MAP = new GobanValuesD(new double[] {
			0.07143662778250448,
			0.23614423350355063,
			0.2255166706132127,
			0.2568890287926289,
			0.2564804713585862,
			0.24520197701778282,
			0.2054500888697867,
			0.19724825882087652,
			0.1634469150475452,
			0.13683348027751532,
			0.1307560199476907,
			0.13674401493207475,
			0.13227496744705725,
			0.14524750114409177,
			0.1632334934586675,
			0.16248691350262778,
			0.1367577878480122,
			0.15796549963297146,
			0.03927543316878937,
			0.24493141247980288,
			0.73897587054248,
			1.059459609735818,
			1.233947656104819,
			1.1982371810540204,
			1.0938691889202088,
			0.9341568146811439,
			0.8670845016143793,
			0.8146995088870159,
			0.7561551509361146,
			0.7429105581932007,
			0.7588152870851673,
			0.7707296212770581,
			0.9115600017596243,
			1.0304444421629833,
			1.1914072213447107,
			0.9517466451860206,
			0.6033786413386959,
			0.15802079642525654,
			0.22435303504095613,
			1.1382755021409252,
			2.11827359205901,
			2.0358925950155298,
			1.936091525982121,
			2.3230501560353787,
			1.5864933077008165,
			1.6415373523264034,
			1.5487043824187492,
			1.5368640703240035,
			1.4519253841827837,
			1.523677411740256,
			1.3685049422430955,
			2.2461494901556427,
			1.5852377745447728,
			2.0689481793469624,
			1.8748232955158604,
			0.8836404843016527,
			0.13869935873812356,
			0.26177589379985133,
			1.318906943011792,
			2.3733715263941635,
			2.7775386530052892,
			1.6706882270773367,
			1.777583344157137,
			1.5395480599912967,
			1.4912803779191692,
			1.4187080706533537,
			1.594510932325082,
			1.375556997547916,
			1.3391022575631182,
			1.3702522271062592,
			1.590398368930233,
			1.3278707376568846,
			2.057439149836627,
			1.8429673907707336,
			1.046989992599036,
			0.1644152975583309,
			0.2657471529379973,
			1.2684812501407996,
			1.7948026539192197,
			1.5490775112243487,
			1.2611391430871515,
			1.429620557079138,
			1.2750018760189692,
			1.2329299841374228,
			1.161870499818993,
			1.1126898785487473,
			1.0926685422993798,
			1.1031613943502185,
			1.0913451409022485,
			1.2611688281166085,
			1.037669039870016,
			1.464678899212336,
			1.6867038132905616,
			1.0223293521804027,
			0.16335079767674762,
			0.25217954136023507,
			1.1280714710677024,
			2.2763277951314342,
			1.8491632981776205,
			1.4167430565020722,
			1.2169361415489333,
			1.1660239130964312,
			1.0930384183524915,
			1.0433622944533383,
			1.004487653138329,
			0.9877560804248915,
			0.9776949653325769,
			0.9908718949629387,
			1.030905512502047,
			1.1946286477743295,
			1.6355007418791676,
			2.150539724570538,
			0.9162555111475453,
			0.15243031123808873,
			0.21588342434132998,
			0.9637426205349363,
			1.6653339620505345,
			1.547892395764045,
			1.2733024740167456,
			1.1727989569664499,
			1.0916382109709517,
			1.035312757689805,
			0.9625892120853651,
			0.9173720550689264,
			0.9037548902978658,
			0.9179285101768762,
			0.9052741308379969,
			0.9756625811095079,
			1.057021569182276,
			1.3504864517077226,
			1.374001244128512,
			0.7701710855796793,
			0.1339397320309021,
			0.2158221495174465,
			0.90631618379701,
			1.6978580876512397,
			1.4928858310522535,
			1.2388230925222208,
			1.1084030731152514,
			1.0274825619385943,
			0.9761550068103867,
			0.9239982635345911,
			0.8872320505210717,
			0.8579884283681339,
			0.8558344029236682,
			0.8654349751502951,
			0.9175598062868228,
			1.0430406529100842,
			1.2972315381664883,
			1.473099601408346,
			0.741498834309611,
			0.12823361295801255,
			0.16946734964457347,
			0.8636674997784485,
			1.5823499164175283,
			1.437381243560979,
			1.182517360884533,
			1.0642323060611418,
			0.9689999769808765,
			0.9226663639552859,
			0.8803857391370958,
			0.8551443212270485,
			0.8360079680868538,
			0.8520951734629537,
			0.8407551112575024,
			0.8923647181216632,
			1.0093054484373292,
			1.2574332030572988,
			1.4094023267400178,
			0.7099474868311608,
			0.11601621600734482,
			0.15278404074506974,
			0.8333829382214035,
			1.6594053664291402,
			1.6565382262839552,
			1.14668788197922,
			1.0098403650490788,
			0.9343188662239826,
			0.8909556316049553,
			0.855861304065861,
			1.0238155084182075,
			0.8492845315771888,
			0.8537877769194381,
			0.8536152345172259,
			0.8928043085726798,
			0.995302671324096,
			1.5084167571448626,
			1.4161056049267793,
			0.717607103553282,
			0.11924877798601956,
			0.14766420832996904,
			0.8276650682138523,
			1.5531406972503579,
			1.4172274528860003,
			1.1410744810042581,
			0.9925056265298097,
			0.9304853655492875,
			0.8768189641663642,
			0.85675882931976,
			0.8463220652712704,
			0.8403823341008204,
			0.8395247796072165,
			0.8483174091416908,
			0.8775850899414046,
			0.9877486078853934,
			1.2576428737243892,
			1.3886856047561906,
			0.7018001330603947,
			0.11850357532157359,
			0.15779149202693607,
			0.8315145689142959,
			1.5941130415748717,
			1.4397600312719137,
			1.156011765115886,
			1.0180584295563493,
			0.9334141614713503,
			0.8856211175256888,
			0.8600146294310695,
			0.8792182061226697,
			0.8460977718700252,
			0.8265363924048157,
			0.8564754589003658,
			0.8946052198957702,
			1.0258976216590336,
			1.259706906350038,
			1.4500534983865003,
			0.7345178120861361,
			0.12748905498324875,
			0.1480254982886774,
			0.8459544749361226,
			1.4562072372273576,
			1.4373831190218729,
			1.1407371031719433,
			1.0382761910302463,
			0.9497984222705522,
			0.8936556799061499,
			0.862471248769089,
			0.8675190517642445,
			0.855195779185317,
			0.8567284702965446,
			0.8773607086279297,
			0.9494718283383756,
			1.0410851918877295,
			1.3190112948280754,
			1.3449043767904996,
			0.7716529341182442,
			0.12309224204671175,
			0.1694318037998242,
			0.9463997061783341,
			2.1257823806995777,
			1.67349863453049,
			1.287631522717314,
			1.072357066388955,
			1.0133873304641214,
			0.9417495008926244,
			0.905187361467591,
			0.9178462829383219,
			0.8963058233651289,
			0.8957974855507309,
			0.9455485399733992,
			1.0107291869922683,
			1.1974482274034157,
			1.592065858793189,
			2.1901548505936828,
			0.9042302610262435,
			0.15587588454857068,
			0.17755501101158505,
			1.0658432097524482,
			1.7216273566720313,
			1.5764956066299671,
			1.1007239105741293,
			1.2625502222810592,
			1.1083589997842516,
			1.0708375328080593,
			1.0170768309070788,
			1.0370575810105458,
			1.0124190065614889,
			1.0377996774428866,
			1.052292770363493,
			1.2376852445372515,
			1.0162353350473765,
			1.412081627755077,
			1.6351531369241672,
			1.0023837962727675,
			0.14853026100574707,
			0.1728583587646819,
			1.1154769599570362,
			2.0514116236530944,
			1.9190086560000486,
			1.4119026091519666,
			1.6206164102337077,
			1.3929327910783091,
			1.296578027957294,
			1.274921553545385,
			1.5545494907263013,
			1.315019347011947,
			1.2870045326973187,
			1.3452864140353435,
			1.6155831127564013,
			1.3560153983343188,
			2.4726635543153392,
			1.859140574307175,
			1.0530601269097448,
			0.1514628664577498,
			0.14701400948141508,
			0.9356056082214589,
			1.8912604501730776,
			2.1125697000959316,
			1.6667189899204669,
			2.083331381980834,
			1.4106508269177103,
			1.4632957761073742,
			1.3938517962202555,
			1.4710978985533334,
			1.3665067265731816,
			1.46611204437582,
			1.3730381363514899,
			2.2444040221452197,
			1.6262982069231333,
			1.9218573052729855,
			1.8513151844245044,
			0.8844570716963617,
			0.12478675026816624,
			0.16931745929346684,
			0.6470760303416273,
			0.9757538340036127,
			1.0902639963052345,
			1.0523760232447235,
			0.9168851678384989,
			0.7731492588705816,
			0.725349240554722,
			0.6913827976146001,
			0.703131974031547,
			0.6903580633647766,
			0.715336711621183,
			0.7503330756326624,
			0.9096955591987627,
			1.0319355214856338,
			1.1268070880805672,
			0.9095042914916908,
			0.5787476273413663,
			0.15129893945401754,
			0.0393139680293378,
			0.16892326085689086,
			0.13773165022109649,
			0.17400810420469515,
			0.1785505584012517,
			0.15835210831374358,
			0.12652339775157406,
			0.12699970621039847,
			0.1090749301115533,
			0.11650506661089524,
			0.10664714598477316,
			0.11897915117848644,
			0.12683914917545944,
			0.1526064287376294,
			0.15992365663036068,
			0.1548214652692905,
			0.11925041901430146,
			0.14795636997230194,
			0.03298437542562825
	});
	// I'm getting paid by LoC, right? 

	@Override
	public GobanValuesD evaluate(Goban gb, int state) {
		GobanValuesD gvd = new GobanValuesD(MAP.getState());
		
		if (state == 0)
			return gvd;
		
//		// encourage tenuki
//		for (int x = 0; x < Goban.WIDTH; x++) {
//			for (int y = 0; y < Goban.HEIGHT; y++) {
//				if (gb.getState(x, y) == -state) {
//					final int radius = 1;
//					for (int dx = Math.max(0, x - radius); dx < Math.min(Goban.WIDTH, x + radius); dx++) {
//						for (int dy = Math.max(0, y - radius); dy < Math.min(Goban.HEIGHT, y + radius); dy++) {
//							double d = gvd.getState(dx, dy);
//							d *= 1.01;
//
//							gvd.setState(dx, dy, d);
//						}
//					}
//				}
//			}
//		}
		return gvd;
	}

}
