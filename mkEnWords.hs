import XMLTools
import Text.XML.HaXml

main = do
	cnt <- readFile "lojban_en.xml"
	let	Document _ _ topElem _ = xmlParse "" cnt
		elems = childrenE $ head $ childrenE topElem
		elems2 = childrenE $ (!! 1) $ childrenE topElem
	putStr $ unlinesM $ reverse $ map (flip getAttr "word") elems
	putStr $ unlinesM $ reverse $ map (flip getAttr "word") elems2
	putStr $ unlines $ reverse $ map getElemText $
		concatMap (filter ("rafsi" `isElemName`) . childrenE) elems

unlinesM :: [Maybe String] -> String
unlinesM [] = ""
unlinesM (Nothing : ls) = unlinesM ls
unlinesM (Just s : ls) = s ++ "\n" ++ unlinesM ls
