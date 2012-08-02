import XMLTools
import Text.XML.HaXml
import System.Environment
import Control.Monad

directory = "./src/main/assets/rafsi/"

main = do
	[lang] <- getArgs
	cnt <- readFile $ "lojban_" ++ lang ++ ".xml"
	let	Document _ _ topElem _ = xmlParse lang cnt
		elems = childrenE $ head $ childrenE topElem
	createDirectoryIfNotExist directory
	createDirectoryIfNotExist $ directory ++ lang ++ "/"
	forM_ "bcdfghjklmnprstvxz" $ \c ->
		makeChrRafsiFiles lang c elems

makeChrRafsiFiles :: String -> Char -> [Element i] -> IO ()
makeChrRafsiFiles lang c = writeFile
	(directory ++ lang ++ "/" ++ [c] ++ ".xml") . makeChrRafsiString c

makeChrRafsiString :: Char -> [Element i] -> String
makeChrRafsiString c = makeXMLString . filterRafsi c

getChildrenByName :: String -> Element i -> [Element i]
getChildrenByName name = filter (isName name) . childrenE

isName :: String -> Element i -> Bool
isName n0 (Elem n1 _ _) = N n0 == n1

filterRafsi :: Char -> [Element i] -> [Element i]
filterRafsi = filter . isHeadRafsi

isHeadRafsi :: Char -> Element i -> Bool
isHeadRafsi h0 = not . null . filter (\(h1 : _) -> h0 == h1) . getRafsi

getRafsi :: Element i -> [String]
getRafsi elem = (\(Elem _ _ [c]) -> showContent c) `map`
	getChildrenByName "rafsi" elem
