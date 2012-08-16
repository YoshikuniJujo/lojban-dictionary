import XMLTools
import Text.XML.HaXml
import System.Environment

completionDirectory = "src/main/assets/completion/"

main = do
	[lang] <- getArgs
	cnt <- readFile $ "lojban_" ++ lang ++ ".xml"
	let	Document _ _ topElem _ = xmlParse "" cnt
		elems = childrenE $ head $ childrenE topElem
		elems2 = childrenE $ (!! 1) $ childrenE topElem
		gismu = unlinesM $ reverse $ map (flip getAttr "word") elems
		naword = unlinesM $ reverse $ map (flip getAttr "word") elems2
		rafsi = unlines $ reverse $ map getElemText $
			concatMap (filter ("rafsi" `isElemName`) . childrenE) elems
		result = concat [gismu, naword, rafsi]
	createDirectoryIfNotExist completionDirectory
	writeFile (completionDirectory ++ lang ++ ".txt") result

unlinesM :: [Maybe String] -> String
unlinesM [] = ""
unlinesM (Nothing : ls) = unlinesM ls
unlinesM (Just s : ls) = s ++ "\n" ++ unlinesM ls
