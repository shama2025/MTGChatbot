//
//  CardViewController.swift
//  MTGChatBot
//
//  Created by Marcus Shaffer on 6/26/25.
//

import Foundation

class CardViewController: ObservableObject {
    func getGenCardData(cardName: String) async throws -> Card {
        var endpoint = ""
        if let encoded = cardName.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed) {
            endpoint = "https://api.scryfall.com/cards/named?exact=\(encoded)"
            print(endpoint)
        }
        // Url endpoint for activity

        guard let url = URL(string: endpoint) else { // Set endpoint to be of type url
            throw CardError.invalidURL
        }

        let (data, response) = try await URLSession.shared.data(from: url)

        guard let response: HTTPURLResponse = response as? HTTPURLResponse, response.statusCode == 200 else {
            throw CardError.invalidResponse
        }

        do {
            let decoder = JSONDecoder() // Decode the JSON
            decoder.keyDecodingStrategy = .convertFromSnakeCase // Helps convert the data from snake to camel case

            let card = try decoder.decode(Card.self, from: data)

            // Substring ruling ID and store in cache
            if let startRange = card.rulingsUri.range(of: "/cards/"),
               let endRange = card.rulingsUri.range(of: "/rulings"),
               startRange.upperBound <= endRange.lowerBound
            {
                let rulingIDSubstring = card.rulingsUri[startRange.upperBound ..< endRange.lowerBound]
                let rulingID = String(rulingIDSubstring) as NSString

                CacheManager.shared.setObject(rulingID, forKey: "rulingsID" as NSString)
            } else {
                print("Could not extract ruling ID from URL")
            }

            // Substring the setid and store in cache
            if let startRange = card.setUri.range(of: "/sets/") {
                let idStartIndex = startRange.upperBound
                let setID = String(card.setUri[idStartIndex ..< card.setUri.endIndex]) as NSString
                CacheManager.shared.setObject(setID, forKey: "setID" as NSString)
            }

            return card
        } catch {
            throw CardError.invalidData
        }
    }
}
